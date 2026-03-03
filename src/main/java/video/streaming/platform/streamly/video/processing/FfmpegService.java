package video.streaming.platform.streamly.video.processing;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FfmpegService {

    public Long getVideoDurationSeconds(Path localVideoPath) {
        try{
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffprobe",
                    "-v", "error",
                    "-show_entries", "format=duration",
                    "-of", "default=noprint_wrappers=1:nokey=1",
                    localVideoPath.toString()
            );

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String output = reader.readLine();

            process.waitFor();

            double seconds = Double.parseDouble(output);
            return Math.round(seconds);

        }catch (Exception e){
            throw new RuntimeException("Erro ao obter duração do Video.");
        }
    }


    public Path generateHls(Path localVideoPath){
        try{
            Path outputDir = Files.createTempDirectory("hls-");

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg",
                    "-i", localVideoPath.toString(),
                    "-profile:v", "baseline",
                    "-level", "3.0",
                    "-start_number", "0",
                    "-hls_time", "10",
                    "-hls_list_size", "0",
                    "-f", "hls",
                    outputDir.resolve("index.m3u8").toString()
            );

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                while(reader.readLine() != null) {
                    // consome somente o output
                }
            }

            int exitCode = process.waitFor();
            if(exitCode != 0){
                throw new RuntimeException("Erro ao gerar HLS com FFmpeg");
            }

            return outputDir;


        }catch (Exception e){
            throw new RuntimeException("Erro ao processar video em HLS");
        }
    }

    public Path generateThumb(Path localFile) {
        try {
            Path outputDir = Files.createTempDirectory("thumb-");
            Path thumbnailPath = outputDir.resolve("thumbnail.jpg");

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg",
                    "-i", localFile.toString(),
                    "-ss", "00:00:03",
                    "-vframes", "1",
                    "-vf", "scale=1280:720:force_original_aspect_ratio=decrease",
                    "-y", // sobrescreve se existir
                    thumbnailPath.toString()
            );

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (BufferedReader reader =
                         new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                while (reader.readLine() != null) {
                    // apenas consome o output
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Erro ao gerar thumbnail com FFmpeg");
            }

            return thumbnailPath;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar thumbnail do vídeo", e);
        }
    }
}
