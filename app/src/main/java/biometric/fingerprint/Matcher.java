package biometric.fingerprint;

import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintImageOptions;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

public class Matcher {

    private Path fingerprintsPath;
    private List<File> probeFiles;
    private File candidateFile;

    private FingerprintImageOptions fingerprintOptions;
    private ArrayList<FingerprintTemplate> probe;
    private FingerprintTemplate candidate;

    public Matcher(Path fingerprintsPath, int dpi) {
        this.fingerprintsPath = fingerprintsPath;
        fingerprintOptions = new FingerprintImageOptions().dpi(dpi);
    }

    public File chooseCandidate(int id) {
        List<File> fingerprint = Arrays.asList(fingerprintsPath.resolve(Integer.toString(id)).toFile().listFiles());
        Collections.shuffle(fingerprint);
        candidateFile = fingerprint.removeLast();
        probeFiles = fingerprint;
        return candidateFile;
    }

    /*
     * Heavy operation for RAM and CPU
     */

    public void buildTemplates() throws IOException {
        candidate = new FingerprintTemplate(new FingerprintImage(Files.readAllBytes(candidateFile.toPath()), fingerprintOptions));

        for (File file : probeFiles) {
            probe.add(new FingerprintTemplate(new FingerprintImage(Files.readAllBytes(file.toPath()), fingerprintOptions)));
        }
    }

    public boolean compare(double threshold) {
        FingerprintMatcher matcher = new FingerprintMatcher(candidate);

        for (FingerprintTemplate t : probe) {
            double similarity = matcher.match(t);
            if (similarity >= threshold) {
                return true;
            }
        }
        return false;
    }
}
