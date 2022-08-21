package com.assignments.finalworks.domain;

import com.assignments.finalworks.config.properties.BatchProperties;
import com.assignments.finalworks.infra.ApiClient;
import com.assignments.finalworks.util.TimeUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class CollectJob {

	private static final String TIME_RESULT = "time_result";
	private static final String ERROR_INFO = "error_info";
	private static final String AVG = "avg";
	private static final String MAX = "max";
	private static final String SUM = "sum";
	private static final String NULL = "null";
	private static final String INTERFACE = "interface";
	private static final int ROOT_INDEX = 0;
	private static final int PATH_1_INDEX = 1;
	private final BatchProperties batchProperties;
	private final ApiClient apiControl;
	private final Executor executor;

	public CollectJob(BatchProperties batchProperties, ApiClient apiControl, Executor executor) {
		this.batchProperties = batchProperties;
		this.apiControl = apiControl;
		this.executor = executor;
	}

	public void batchCollect(long runTime) {
		List<CompletableFuture<String>> completableFutures = new ArrayList<>();
		this.batchProperties.getIpList().forEach(ip -> completableFutures.add(CompletableFuture.supplyAsync(() -> {
			processData(runTime, ip);
			return "Thread-" + ip + " : Collect Scheduler Work Complete";
		}, this.executor)));

		CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();
		long endTime = System.currentTimeMillis() / 1000;
		String start = TimeUtils.unixTimeToDateForm(runTime);
		String end = TimeUtils.unixTimeToDateForm(endTime);
		System.out.println("작업 시작 : " + start);
		System.out.println("작업 종료 : " + end);
		System.out.println("작업 소요 시간 : " + (endTime - runTime) + "(s)");

		String resultValue = String.format("작업 시작 : %s%n작업종료 : %s%n작업 소요 시간 : %d(s)", start, end, (endTime - runTime));
		this.makeResultFile(resultValue, runTime);
	}

	public void makeFile(String ip, String fieldName, String valueType, String value, long runTime) {
		this.makeFile(ip, ((runTime) - 300), ip, String.format("%s : %s(%s)%n", fieldName, value, valueType));
	}

	public void makeResultFile(String value, long runTime) {
		this.makeFile(String.format("%s_", TIME_RESULT), ((runTime) - 300), String.format("%s/", TIME_RESULT), value);
	}

	public void makeErrorFile(String value, long runTime) {
		this.makeFile(String.format("%s_", ERROR_INFO), ((runTime) - 300), String.format("%s/", ERROR_INFO), value);
	}


	// fieldList 중 하나의 설정 경로에 따라 데이터에 접근
	public List<Long> accessData(JSONObject response, List<String> route) {
		JSONObject obj = response;
		List<Long> values = new ArrayList<>();
		// cpu-* : cpu 전체 수집 시 따로 처리
		if (route.get(0).equals("cpu-*")) {
			values.addAll(getAllCpu(response, route));
			// if_octets-em* : interface octets 전체 수집 시 따로 처리
		} else if (route.get(1).equals("if_octets-em*.rrd")) {
			values.addAll(getAllInterface(response, route));
		} else {
			for (int i = 0; i < route.size() - 1; i++) {
				if (obj != null) {
					obj = (JSONObject) obj.get(route.get(i));
					if (i == route.size() - 2) {
						values.addAll((List<Long>) obj.get(route.get(i + 1)));
					}
				}
			}
		}
		System.out.println(values);
		return values;
	}


	public List<Long> getAllCpu(JSONObject response, List<String> route) {
		List<Long> values = new ArrayList<>();
		List<String> keys = new ArrayList<>(response.keySet());
		List<String> newRoute = new ArrayList<>();
		int n = 0;
		newRoute.addAll(route);
		for (int i = 0; i < keys.size(); i++) {
			if (keys.get(i).contains("cpu-")) {
				newRoute.set(0, "cpu-" + n);
				values.addAll(accessData(response, newRoute));
				n++;
			}
		}
		return values;
	}

	public List<Long> getAllInterface(JSONObject response, List<String> route) {
		List<Long> values = new ArrayList<>();
		List<String> keys = new ArrayList<>(response.keySet());
		List<String> newRoute = new ArrayList<>();
		int n = 1;
		newRoute.addAll(route);
		for (int i = 0; i < keys.size(); i++) {
			if (keys.get(i).contains("interface")) {
				newRoute.set(1, "if_octets-em" + n + ".rrd");
				values.addAll(accessData(response, newRoute));
				n++;
			}
		}
		return values;
	}


	// 데이터 설정한 형태로 가공
	public void processData(long runTime, String ip) {
		List<Long> allValues = new ArrayList<>();
		this.batchProperties.getFieldList()
			.forEach(field -> {
				List<String> splitField = Arrays.asList(field.split(" : "));
				List<String> routeKeys = Arrays.asList(splitField.get(0).split("/"));
				allValues.addAll(accessData(getData(runTime, ip), routeKeys));
				String returnType = splitField.get(1);
				String saveName = splitField.get(2);

				if (allValues.isEmpty()) {
					this.makeFile(ip, saveName, returnType, NULL, runTime);
					String errorMessage = String.format(
						"<ERROR>%nip : %s%ndata : %s%nreturnType : %s%nDATA IS NULL.%n", ip, saveName,
						returnType);
					this.makeErrorFile(errorMessage, runTime);
					return;
				}

				if (AVG.equals(returnType)) {
					this.makeFile(ip, saveName, returnType, String.valueOf(
						BigDecimal.valueOf(this.getSumOfValues(allValues) / allValues.size())
							.setScale(2, RoundingMode.HALF_DOWN)), runTime);
				}
				if (MAX.equals(returnType)) {
					this.makeFile(ip, saveName, returnType, String.valueOf(getMaxOfValues(allValues)), runTime);
				}
				if (SUM.equals(returnType)) {
					this.makeFile(ip, saveName, returnType, String.valueOf(getSumOfValues(allValues)), runTime);
				}
			});
	}

	public double getMaxOfValues(List<Long> values) {
		return Collections.max(values);
	}

	public double getSumOfValues(List<Long> values) {
		return values.stream()
			.reduce(0L, Long::sum);
	}

	public JSONObject getData(long runTime, String ip) {
		return apiControl.requestApi(runTime, ip);
	}

	private void makeFile(String fileNamePrefix, long time, String directoryPrefix, String value) {
		String fileName = String.format("%s/%d.txt", fileNamePrefix, time);
		String directory = String.format("%s%s/", this.batchProperties.getSaveDir(), directoryPrefix);
		String fileDirectory = String.format("%s%s", directory, fileName);

		try {
			File storage = new File(directory);
			if (!storage.exists()) {
				storage.mkdirs();
			}
			File file = new File(fileDirectory);
			if (!file.exists()) {
				file.createNewFile();
			}

			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
				writer.write(value);
				writer.flush();
			}
		} catch (Exception e) {
			// TODO: throw exception with log message
			e.printStackTrace();
		}
	}
}
