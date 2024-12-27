package nl.gertjanidema.netex.chb_dataload.ndov;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;
import org.springframework.beans.factory.annotation.Value;

//import nl.gertjanidema.netex.dataload.dto.NetexFileInfo;

public class NdovService {

//    private static Logger LOG = LoggerFactory.getLogger(NdovService.class);

    private final static String CHB_PREFIX = "ExportCHB";
    private final static String PSA_PREFIX = "PassengerStopAssignmentExportCHB";
//    private final static String username = "anonymous";
//    private final static String password = "anonymous@ndovloket.nl";

    @Value("${ndov.server.ftp}")
    private String FTP_SERVER;

    @Value("${ndov.server.usesftp}")
    private boolean useSftp = true;
    
    @Value("${ndov.username}")
    private String username;

    @Value("${ndov.password}")
    private String password;

    @Value("${osm_netex.path.temp}")
    private Path tempPath;

    private Path chbTempPath;

    public Path getChbTempPath() {
        if (chbTempPath == null) {
            this.chbTempPath = tempPath;
        }
        return chbTempPath;
    }
    
    /**
     * Initialize the CHB context. Create temporary folders if necessary and clear
     * any old temporary files.
     */
    public void initializeChb() {
        var folder = getChbTempPath();
        if (!folder.toFile().exists()) {
            folder.toFile().mkdir();
        }
        else {
            folder.forEach(p -> {
                var file = p.toFile();
                if (file.isFile()) file.delete();
            });
        }
    }

    public FTPClient connect() throws IOException {
        FTPClient ftpClient;
        if (useSftp) {
            ftpClient = new FTPSClient();
        }
        else {
            ftpClient = new FTPClient();
        }
        ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        ftpClient.connect(InetAddress.getByName(FTP_SERVER));
        ftpClient.login(username, password);
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        return ftpClient;
    }

    public File downloadChbFile() throws IOException {
        List<String> chbFiles = getChbFiles().stream().filter(f -> f.startsWith(CHB_PREFIX))
        .sorted((String::compareTo)).collect(Collectors.toList());
        if (chbFiles.size() == 0) return null;
        var fileName = chbFiles.get(chbFiles.size() - 1);
        var sourceFile = new File("haltes/" + fileName);
        var targetName = CHB_PREFIX + ".xml.gz";
        downloadFile(sourceFile, getChbTempPath(), targetName);
        return getChbTempPath().resolve(targetName).toFile();
    }
    
    public File downloadPsaFile() throws IOException {
        List<String> chbFiles = getChbFiles().stream().filter(f -> f.startsWith(PSA_PREFIX))
        .sorted((String::compareTo)).collect(Collectors.toList());
        if (chbFiles.size() == 0) return null;
        var fileName = chbFiles.get(chbFiles.size() - 1);
        var sourceFile = new File("haltes/" + fileName);
        var targetName = PSA_PREFIX + ".xml.gz";
        downloadFile(sourceFile, getChbTempPath(), targetName);
        return getChbTempPath().resolve(targetName).toFile();
    }
    
    private List<String> getChbFiles() {
        List<String> files;
        FTPClient ftpClient = null;
        try {
            ftpClient = connect();
           ftpClient.changeWorkingDirectory("haltes");
           ftpClient.enterLocalPassiveMode();
//            ftpClient.listDirectories();
            // This hack is required to trigger the detection of the FTP server type.
            // just listFiles is somehow not enough
             FTPFile[] ftpFiles = ftpClient.listFiles();
            
            files = new ArrayList<>(ftpFiles.length);
            for (FTPFile file : ftpFiles) {
                files.add(file.getName());
            }
        } catch (@SuppressWarnings("unused") IOException e) {
            return Collections.emptyList();
        } finally {
            close(ftpClient);
        }
        return files;
    }

    
    public void downloadFile(File sourceFile, Path targetPath) throws IOException {
        downloadFile(sourceFile, targetPath, sourceFile.getName());
    }
    
    public void downloadFile(File sourceFile, Path targetPath, String targetName) throws IOException {
        var ftpClient = connect();
        ftpClient.enterLocalPassiveMode();
        var targetFile = new File(targetPath.toFile(), targetName);
        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            var succes  = ftpClient.retrieveFile(sourceFile.toString(), outputStream);
            if (succes) {
                outputStream.close();
            }
            else {
                throw new FileNotFoundException(sourceFile.getName());
            }
        }
        finally {
            close(ftpClient);
        }
    }

    private static void close(FTPClient ftpClient) {
        if (ftpClient != null) {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
