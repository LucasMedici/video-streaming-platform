package video.streaming.platform.streamly.video.processing;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
}
