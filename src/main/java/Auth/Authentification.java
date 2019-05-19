package Auth;

import Database.DBControl;
import Database.DBManager;
import Database.LoggedUser;
import View.MensagemType;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.apache.commons.io.FileUtils;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.xml.bind.DatatypeConverter;
import javax.xml.ws.Service;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

import static Util.Util.byteToHex;


public class Authentification {

	public static boolean acessarArquivo(HashMap user, String index, String nomeArquivo, PrivateKey chavePrivada, String pastaArquivos) {
		try {
			String[] linhasIndex = index.split("\n");
			for (String linha: linhasIndex) {
				String[] params = linha.split(" ");
				String nomeSecreto = params[1];

				if (nomeSecreto.equals(nomeArquivo)) {
					String email = params[2];
					String grupo = params[3];
					if (user.get("email").equals(email) || user.get("groupName").equals(grupo)) {
						String nomeCodigoArquivo = params[0];
						byte[] conteudoArquivo = Authentification.decriptaArquivo(user, pastaArquivos, nomeCodigoArquivo, chavePrivada);
						FileUtils.writeByteArrayToFile(new File(pastaArquivos + File.separator + nomeSecreto),conteudoArquivo);
						return true;
					}
					else {
						return false;
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static byte[] decriptaArquivo(HashMap user, String caminho, String filename, PrivateKey chavePrivada) {
		try {

			byte[] arqEnv = FileUtils.readFileToByteArray(new File(caminho + File.separator + filename + ".env"));
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, chavePrivada);
			cipher.update(arqEnv);

			byte [] semente = LoggedUser.getInstance().getSecretWord().getBytes();

			byte[] arqEnc = FileUtils.readFileToByteArray(new File(caminho + File.separator + filename + ".enc"));
			SecureRandom rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
			rand.setSeed(arqEnc);

			KeyGenerator keyGen = KeyGenerator.getInstance("DES");
			keyGen.init(56, rand);
			Key chaveSecreta = keyGen.generateKey();

			cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, chaveSecreta);
			byte[] index = cipher.doFinal(arqEnc);

			X509Certificate cert = Authentification.leCertificadoDigital(((String) user.get("certificado")).getBytes());
			Signature assinatura = Signature.getInstance("MD5withRSA");
			assinatura.initVerify(cert.getPublicKey());
			assinatura.update(index);

			byte[] arqAsd = FileUtils.readFileToByteArray(new File(caminho + File.separator + filename + ".asd"));
			if (assinatura.verify(arqAsd) == false) {
				System.out.println(filename + " pode ter sido adulterado");
				DBManager.insereRegistro(8005, (String) user.get("email"));
				return null;
			}
			else {
				System.out.println("Decriptou index ok");
				return index;
			}
		}
		catch (Exception IOError) {
			DBManager.insereRegistro(8008, (String) user.get("email"));
			IOError.printStackTrace();
			return null;
		}
	}

	public static String getKey(String filename) {
		String strKeyPEM = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));
			String line;
			strKeyPEM = "";
			while ((line = br.readLine()) != null) {
				strKeyPEM += line + "\n";
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return strKeyPEM;
	}

	public static PrivateKey leChavePrivada(String fraseSecreta, String pathString, HashMap user) {
		try {
			SecureRandom rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
			rand.setSeed(fraseSecreta.getBytes());

			KeyGenerator keyGen = KeyGenerator.getInstance("DES");
			keyGen.init(56, rand);
			Key chave = keyGen.generateKey();

			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			try {
				cipher.init(Cipher.DECRYPT_MODE, chave);
			}
			catch (Exception e) {
				DBControl.getInstance().insertRegister(MensagemType.CHAVE_PRIVADA_VERIFICADA_NEGATIVAMENTE_FRASE_SECRETA, LoggedUser.getInstance().getEmail());
				return null;
			}

			byte[] bytes = null;
			try {
				bytes = FileUtils.readFileToByteArray(new File(pathString));
			}
			catch (Exception e) {
				DBControl.getInstance().insertRegister(MensagemType.CHAVE_PRIVADA_VERIFICADA_NEGATIVAMENTE_CAMINHO_INVALIDO, LoggedUser.getInstance().getEmail());
				return null;
			}

			String chavePrivadaBase64 = new String(cipher.doFinal(bytes), "UTF8");
			chavePrivadaBase64 = chavePrivadaBase64.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "").trim();
			byte[] chavePrivadaBytes = DatatypeConverter.parseBase64Binary(chavePrivadaBase64);

			KeyFactory factory = KeyFactory.getInstance("RSA");
			return factory.generatePrivate(new PKCS8EncodedKeySpec(chavePrivadaBytes));
		}
		catch (Exception e) {
			DBControl.getInstance().insertRegister(MensagemType.CHAVE_PRIVADA_VERIFICADA_NEGATIVAMENTE_ASSINATURA_DIGITAL, LoggedUser.getInstance().getEmail());
			return null;
		}
	}

	public static boolean testaChavePrivada(PrivateKey chavePrivada, HashMap user) {
		try {
			byte[] teste = new byte[1024];
//			SecureRandom.getInstance("MD5withRSA").nextBytes(teste);
			Signature assinatura = Signature.getInstance("MD5withRSA");
			assinatura.initSign(chavePrivada);
			assinatura.update(teste);
			byte[] resp = assinatura.sign();

			PublicKey chavePublica = Authentification.leCertificadoDigital(((String) user.get("certificate")).getBytes()).getPublicKey();
			assinatura.initVerify(chavePublica);
			assinatura.update(teste);

			if (assinatura.verify(resp)) {
				System.out.println("Chave válida!");
				return true;
			}  else {
				System.out.println("Chave rejeitada!");
				return false;
			}
		}
		catch (Exception e) {
			System.out.println("Erro ao testar chave privada");
			return false;
		}
	}

	public static X509Certificate leCertificadoDigital(byte[] bytes) {
		try {

			InputStream stream = new ByteArrayInputStream(bytes);
			CertificateFactory factory = CertificateFactory.getInstance("X.509");
			X509Certificate cert = (X509Certificate) factory.generateCertificate(stream);
			stream.close();
			return cert;
		}
		catch (IOException e) {
			System.out.println("Certificado digital inválido");
			return null;
		} catch (CertificateException e) {
			System.out.println("Certificado digital inválido");
			return null;
		}
	}

	public static String certToString(X509Certificate cert) {
	    StringWriter sw = new StringWriter();
	    try {
	        sw.write("-----BEGIN CERTIFICATE-----\n");
	        sw.write(DatatypeConverter.printBase64Binary(cert.getEncoded()).replaceAll("(.{64})", "$1\n"));
	        sw.write("\n-----END CERTIFICATE-----\n");
	    } catch (CertificateEncodingException e) {
	        e.printStackTrace();
	    }
	    return sw.toString();
	}


	public static boolean cadastraUsuario(String grupo, String senha, String pathCert) {
		if (Authentification.verificaRegrasSenha(senha) == false)
			return false;

		byte[] certDigBytes = null;
		try {
			certDigBytes = FileUtils.readFileToByteArray(new File(pathCert));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		X509Certificate cert = leCertificadoDigital(certDigBytes);
		String subjectDN = cert.getSubjectDN().getName();
		int start = subjectDN.indexOf("=");
		int end = subjectDN.indexOf(",");
		String email = subjectDN.substring(start + 1, end);

		start = subjectDN.indexOf("=", end);
		end = subjectDN.indexOf(",", start);
		String nome = subjectDN.substring(start + 1, end);

		String salt = Authentification.geraSalt();
		String senhaProcessada = Authentification.geraSenhaProcessada(senha, salt);

		boolean ret = DBManager.addUser(nome, email, grupo, salt, senhaProcessada, certToString(cert));
		if (ret) {
			DBManager.insereRegistro(6005, email);
		}
		return ret;
	}

	public static HashMap autenticaEmail(String email) {
		List<HashMap> list = null;
		try {
			list = DBManager.getUser(email);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if (list.size() == 1)
			return list.get(0);
		return null;
	}

	public static boolean autenticaSenha(String senha, HashMap user)  {
		String senhaDigest = Authentification.geraSenhaProcessada(senha, (String) user.get("salt"));
		if (user.get("passwordDigest").equals(senhaDigest))
			return true;
		return false;
	}

	public static String geraSenhaProcessada(String senha, String salt) {
		MessageDigest sha1 = null;
		try {
			sha1 = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Não encontrou algoritmo SHA1");
			return null;
		}
		sha1.update((senha + salt).getBytes());
		return byteToHex(sha1.digest());
	}

	private static String geraSalt() {
		SecureRandom rand = new SecureRandom();
		StringBuffer salt = new StringBuffer();
		for (int i = 0; i < 10; i++) {
			salt.append(new Integer(rand.nextInt(9)).toString());
		}
		return salt.toString();
	}

	public static boolean verificaRegrasSenha(String senha) {
		int len = senha.length();
		if (len < 6 || len > 8)
			return false;

		for (int i = 0; i < len; ++i) {
	        if (!Character.isDigit(senha.charAt(i))) {
	            return false;
	        }
	    }

		boolean crescente = true;
		boolean decrescente = true;

		for (int i = 0; i < len - 1; i++) {
			char c = senha.charAt(i);
			char cProx = senha.charAt(i+1);

			if (Character.getNumericValue(cProx) != Character.getNumericValue(c) + 1)
				crescente = false;
			if (Character.getNumericValue(cProx) != Character.getNumericValue(c) - 1)
				decrescente = false;
			if (cProx == c )
				return false;
		}
		return (!crescente) && (!decrescente);
	}

	public static boolean conferirSenha(String senha, String confirmacao, HashMap user) {

		if (senha.length() >= 6 && senha.length() <= 8 && isNumeric(senha)) {

			System.out.println("Passou primeira parte");

			if( !checkIfIsCrescente(senha) ) {

				return false;

			}

			if( !checkIfIsDecrescente(senha) ) {

				return false;

			}

			if( !checkIfHasConsecutivo(senha) ) {

				return false;

			}

			return true;

		}

		return false;
	}

	private static boolean isNumeric(String str)
	{
		for (char c : str.toCharArray())
		{
			if (!Character.isDigit(c)) return false;
		}
		return true;
	}

	private static boolean checkIfIsCrescente(String str)
	{
		Integer last = null;
		Integer repeater = 0;

		for (Character c : str.toCharArray())
		{
			Integer atual = Integer.parseInt(c.toString());

			if (last == null) {

				last = atual;

			} else {

				if( atual == (last + 1)) {

					last = atual;
					repeater = repeater + 1;

					if (repeater == 2) {

						return false;


					}

				} else {

					repeater = 0;

				}

			}

		}

		return true;
	}

	private static boolean checkIfIsDecrescente(String str)
	{
		Integer last = null;
		Integer repeater = 0;

		for (Character c : str.toCharArray())
		{
			Integer atual = Integer.parseInt(c.toString());

			if (last == null) {

				last = atual;

			} else {

				if( atual == (last - 1)) {

					last = atual;
					repeater = repeater + 1;

					if (repeater == 2) {

						return false;


					}

				} else {

					repeater = 0;

				}

			}

		}

		return true;
	}

	private static boolean checkIfHasConsecutivo(String str)
	{
		Integer last = null;

		for (Character c : str.toCharArray())
		{
			Integer atual = Integer.parseInt(c.toString());

			if (last == null) {

				last = atual;

			} else {

				if( atual == last ) {

					return false;

				} else {

					return true;

				}

			}

		}

		return true;
	}

	/* **************************************************************************************************
	 **
	 **  Increment Wrong Access Password
	 **
	 ****************************************************************************************************/

	public static void incrementWrongAccessPassowrd() {

		HashMap user = LoggedUser.getInstance().getUser();

		DBControl.getInstance().incrementWrongAccessPassword((String)user.get("email"));

		user = LoggedUser.getInstance().getUser();

		int wrongAccess = ((Integer) user.get("numberWrongAccessPassword"));

		switch (wrongAccess){
			case 1:
				DBManager.insereRegistro(MensagemType.PRIMEIRO_ERRO_SENHA_PESSOAL_CONTABILIZADO, LoggedUser.getInstance().getEmail());
				break;
			case 2:
				DBManager.insereRegistro(MensagemType.SEGUNDO_ERRO_SENHA_PESSOAL_CONTABILIZADO, LoggedUser.getInstance().getEmail());
				break;
			case 3:
				DBManager.insereRegistro(MensagemType.TERCEIRO_ERRO_SENHA_PESSOAL_CONTABILIZADO, LoggedUser.getInstance().getEmail());
				break;
		}
	}

	/* **************************************************************************************************
	 **
	 **  Should Block User For Password
	 **
	 ****************************************************************************************************/

	public static boolean shouldBlockUserForPassword() {

		HashMap user = LoggedUser.getInstance().getUser();

		int wrongAccess = ((Integer) user.get("numberWrongAccessPassword"));

		System.out.println("\nshouldBlockUserForPassword\n");
		System.out.println(wrongAccess);
		System.out.println("\n");

		return wrongAccess >= 3;

	}

	/* **************************************************************************************************
	 **
	 **  Increment Wrong Access For Private Key
	 **
	 ****************************************************************************************************/

	public static void incrementWrongAccessPrivateKey() {

		HashMap user = LoggedUser.getInstance().getUser();

		DBControl.getInstance().incrementWrongAccessPrivateKey((String)user.get("email"));

		user = LoggedUser.getInstance().getUser();

		int wrongAccess = ((Integer) user.get("numberWrongAccessPrivateKey"));

		System.out.println("\nincrementWrongAccessPrivateKey\n");
		System.out.println(wrongAccess);
		System.out.println("\n");

		if (wrongAccess == 1)
			DBManager.insereRegistro(3004, (String) user.get("email"));

		else if (wrongAccess == 2)
			DBManager.insereRegistro(3005, (String) user.get("email"));

		else if (wrongAccess == 3)
			DBManager.insereRegistro(3006, (String) user.get("email"));

	}

	/* **************************************************************************************************
	 **
	 **  Should Block User For Private Key
	 **
	 ****************************************************************************************************/

	public static boolean shouldBlockUserForPrivateKey() {

		HashMap user = LoggedUser.getInstance().getUser();

		int wrongAccess = ((Integer) user.get("numberWrongAccessPrivateKey"));

		System.out.println("\nshouldBlockUserForPrivateKey\n");
		System.out.println(wrongAccess);
		System.out.println("\n");

		return wrongAccess >= 3;

	}


}
