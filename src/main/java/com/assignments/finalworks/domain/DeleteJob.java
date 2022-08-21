package com.assignments.finalworks.domain;

import com.assignments.finalworks.config.properties.BatchProperties;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DeleteJob {

	private final BatchProperties batchProperties;

	public void deleteFile(long runtime) {

		String fileToDeleteDirectory = this.batchProperties.getSaveDir();

		File dataFolder = new File(fileToDeleteDirectory);
		try {
			List<String> folders = Arrays.asList(dataFolder.list());

			folders.forEach(folder -> {
				File checkFolder = new File(fileToDeleteDirectory + folder);
				List<String> checkFiles = Arrays.asList(checkFolder.list());
				checkFiles.forEach(checkFile->{
					String orgFileName = checkFile.substring(0, checkFile.length() - 4);
					if (checkFile.contains("time_result_") || checkFile.contains("error_result_")
						|| checkFile.contains("log_data_")) {
						return;
					}
					long ttl = this.batchProperties.getTtl();
					if (runtime - (Long.parseLong(orgFileName) + 240) > ttl) {
						File deleteFile = new File(
							fileToDeleteDirectory + folder + "/" + checkFile);
						deleteFile.delete();
					}
				});
			});
		} catch (Exception e) {
			System.out.println("폴더 삭제 실패 / errorMessage : " + e.getMessage());
		}

	}
}
