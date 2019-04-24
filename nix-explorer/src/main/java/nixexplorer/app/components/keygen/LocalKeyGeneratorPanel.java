package nixexplorer.app.components.keygen;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JDialog;
import javax.swing.JFileChooser;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.KeyPair;

import nixexplorer.app.session.SessionInfo;

public class LocalKeyGeneratorPanel extends KeyGeneratorPanel {

	public LocalKeyGeneratorPanel(SessionInfo info, JDialog dlg) {
		super(info, dlg);
	}

	@Override
	protected void loadPublicKey() throws Exception {
		try {
			Path defaultPath = this.pubKeyPath == null
					? Paths.get(System.getProperty("user.home"), ".ssh", "id_rsa.pub").toAbsolutePath()
					: Paths.get(this.pubKeyPath);
			byte[] bytes = Files.readAllBytes(defaultPath);
			this.pubKey = new String(bytes, "utf-8");
			this.pubKeyPath = defaultPath.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void generateKeys(String passPhrase) throws Exception {
		Path sshDir = Paths.get(System.getProperty("user.home"), ".ssh");
		Path pubKeyPath = Paths.get(System.getProperty("user.home"), ".ssh", "id_rsa.pub").toAbsolutePath();
		Path keyPath = Paths.get(System.getProperty("user.home"), ".ssh", "id_rsa").toAbsolutePath();
		JSch jsch = new JSch();
		KeyPair kpair = KeyPair.genKeyPair(jsch, KeyPair.RSA);
		Files.createDirectories(sshDir);
		if (passPhrase.length() > 0) {
			kpair.writePrivateKey(keyPath.toString(), passPhrase.getBytes("utf-8"));
		} else {
			kpair.writePrivateKey(keyPath.toString());
		}
		kpair.writePublicKey(pubKeyPath.toString(), System.getProperty("user.name") + "@localcomputer");
		kpair.dispose();
	}

	@Override
	protected void cleanup() {
	}

	@Override
	protected void selectKeyFile() {
		JFileChooser jfc = new JFileChooser();
		if (jfc.showOpenDialog(super.dlg) == JFileChooser.APPROVE_OPTION) {
			this.pubKeyPath = jfc.getSelectedFile().getAbsolutePath();
			loadKeys();
		}
	}

}
