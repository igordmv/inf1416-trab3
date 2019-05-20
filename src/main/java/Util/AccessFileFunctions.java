package Util;

import Database.DBControl;
import Database.LoggedUser;
import org.apache.commons.io.FileUtils;

import javax.crypto.*;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

import static Util.Util.byteToHex;

public class AccessFileFunctions {

	/* **************************************************************************************************
	 **
	 **  Read File
	 **
	 ****************************************************************************************************/

	public static boolean readFile(String index, String fileName, PrivateKey privateKey, String folder) {
		try {

			String[] linhasIndex = index.split("\n");

			for (String linha: linhasIndex) {

				String[] params = linha.split(" ");

				String nomeSecreto = params[1];

				if (nomeSecreto.equals(fileName)) {
					String email = params[2];
					String grupo = params[3];

					String groupName = "";

					HashMap user = LoggedUser.getInstance().getUser();

					Integer id = (Integer) user.get("grupoId");

					if( id == 1 ) {
						groupName = "Administrador";
					} else {
						groupName = "Usuário";
					}

					if (user.get("email").equals(email) || groupName.equals(grupo)) {

						DBControl.getInstance().insertRegister(MensagemType.ACESSO_PERMITIDO_AO_ARQUIVO, LoggedUser.getInstance().getEmail(), fileName);

						String nomeCodigoArquivo = params[0];
						byte[] conteudoArquivo = AccessFileFunctions.decryptFile(user, folder, nomeCodigoArquivo, privateKey);
						FileUtils.writeByteArrayToFile(new File(folder + File.separator + nomeSecreto),conteudoArquivo);

						return true;

					} else {

						DBControl.getInstance().insertRegister(MensagemType.ACESSO_NEGADO_AO_ARQUIVO, LoggedUser.getInstance().getEmail(), fileName);

						return false;

					}
				}
			}
		}
		catch (Exception e) {
			DBControl.getInstance().insertRegister(MensagemType.FALHA_NA_DECRIPTACAO_DO_ARQUIVO, LoggedUser.getInstance().getEmail());
			e.printStackTrace();
		}
		return false;
	}

	/* **************************************************************************************************
	 **
	 **  Decrypt File
	 **
	 ****************************************************************************************************/

	public static byte[] decryptFile(HashMap user, String path, String filename, PrivateKey privateKey) {
		try {

			Cipher cipher = null;
			byte[] arqEnv = FileUtils.readFileToByteArray(new File(path + File.separator + filename + ".env"));

			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			cipher.update(arqEnv);
			byte [] seed = cipher.doFinal();
			String sd = new String(seed);

			System.out.println(sd);

			byte[] arqEnc = FileUtils.readFileToByteArray(new File(path + File.separator + filename + ".enc"));
			SecureRandom rand = SecureRandom.getInstance("SHA1PRNG");
			rand.setSeed(seed);

			KeyGenerator keyGen = KeyGenerator.getInstance("DES");
			keyGen.init(56, rand);
			Key key = keyGen.generateKey();

			byte[] arqAsd = FileUtils.readFileToByteArray(new File(path + File.separator + filename + ".asd"));

			X509Certificate cert = AccessFileFunctions.readDigitalCertificate(((String) user.get("certificate")).getBytes());

			Signature signature = Signature.getInstance("MD5withRSA");
			signature.initVerify(cert.getPublicKey());
			signature.update(arqAsd);

			cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key);

			byte[] fileContent = cipher.doFinal(arqEnc);

			if( filename == "index" ) {

				DBControl.getInstance().insertRegister(MensagemType.ARQUIVO_DE_INDICE_DECRIPTADO_COM_SUCESSO, LoggedUser.getInstance().getEmail());

			} else {

				DBControl.getInstance().insertRegister(MensagemType.ARQUIVO_DECRIPTADO_COM_SUCESSO, LoggedUser.getInstance().getEmail(), filename);

			}

			if (signature.verify(arqAsd)) {

				if( filename == "index" ) {

					DBControl.getInstance().insertRegister(MensagemType.FALHA_NA_VERIFICACAO_INTEGRIDADE_E_AUTENTICIDADE, LoggedUser.getInstance().getEmail());

				} else {

					DBControl.getInstance().insertRegister(MensagemType.FALHA_NA_VERIFICACA_INGREDADE_AUTENTICIDADE_ARQUIVO, LoggedUser.getInstance().getEmail(), filename);

				}

				return null;

			}  else {

				if( filename == "index" ) {

					DBControl.getInstance().insertRegister(MensagemType.ARQUIVO_DE_INDICE_VERIFICADO_INTEGRIDADE_E_AUTENTIFICIDADE, LoggedUser.getInstance().getEmail());

				} else {

					DBControl.getInstance().insertRegister(MensagemType.ARQUIVO_VERIFICADO_INTEGRIDADE_E_AUTENTICIDADE, LoggedUser.getInstance().getEmail(), filename);

				}

				System.out.println("Decriptou index");

				return fileContent;

			}

		}
		catch (Exception IOError) {

			if( filename == "index" ) {

				DBControl.getInstance().insertRegister(MensagemType.FALHA_NA_DECRIPTACAO_DO_ARQUIVO_DE_INDICE, LoggedUser.getInstance().getEmail());

			} else {

				DBControl.getInstance().insertRegister(MensagemType.FALHA_NA_DECRIPTACAO_DO_ARQUIVO, LoggedUser.getInstance().getEmail(), filename);

			}

			IOError.printStackTrace();
			return null;
		}
	}

	/* **************************************************************************************************
	 **
	 **  Decrypt File
	 **
	 ****************************************************************************************************/

	public static PrivateKey readPrivateKey(String seed, String path) {

		HashMap user = LoggedUser.getInstance().getUser();

		SecureRandom rand = null;
		try {
			rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			return null;
		}
		rand.setSeed(seed.getBytes());

		KeyGenerator keyGen = null;
		try {
			keyGen = KeyGenerator.getInstance("DES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}

		keyGen.init(56, rand);
		Key chave = keyGen.generateKey();

		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		}
		try {
			cipher.init(Cipher.DECRYPT_MODE, chave);
		}
		catch (Exception e) {
			return null;
		}

		byte[] bytes = null;
		try {
			bytes = FileUtils.readFileToByteArray(new File(path));
		}
		catch (Exception e) {
			DBControl.getInstance().insertRegister(MensagemType.CHAVE_PRIVADA_VERIFICADA_NEGATIVAMENTE_CAMINHO_INVALIDO, LoggedUser.getInstance().getEmail());
			return null;
		}

		String chavePrivadaBase64 = null;
		try {
			chavePrivadaBase64 = new String(cipher.doFinal(bytes), "UTF8");
		} catch (UnsupportedEncodingException e) {
			DBControl.getInstance().insertRegister(MensagemType.CHAVE_PRIVADA_VERIFICADA_NEGATIVAMENTE_CAMINHO_INVALIDO, LoggedUser.getInstance().getEmail());
			e.printStackTrace();
			return null;
		} catch (IllegalBlockSizeException e) {
			DBControl.getInstance().insertRegister(MensagemType.CHAVE_PRIVADA_VERIFICADA_NEGATIVAMENTE_CAMINHO_INVALIDO, LoggedUser.getInstance().getEmail());
			e.printStackTrace();
			return null;
		} catch (BadPaddingException e) {
			e.printStackTrace();
			DBControl.getInstance().insertRegister(MensagemType.CHAVE_PRIVADA_VERIFICADA_NEGATIVAMENTE_ASSINATURA_DIGITAL, LoggedUser.getInstance().getEmail());
			return null;
		}
		chavePrivadaBase64 = chavePrivadaBase64.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "").trim();
		byte[] chavePrivadaBytes = DatatypeConverter.parseBase64Binary(chavePrivadaBase64);

		KeyFactory factory = null;
		try {
			factory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		try {
			return factory.generatePrivate(new PKCS8EncodedKeySpec(chavePrivadaBytes));
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			return null;
		}

	}

	/* **************************************************************************************************
	 **
	 **  Verify Private Key
	 **
	 ****************************************************************************************************/

	public static boolean verifyPrivateKey(PrivateKey privateKey) {

		HashMap user = LoggedUser.getInstance().getUser();

		try {
			byte[] teste = new byte[1024];

			Signature assinatura = Signature.getInstance("MD5withRSA");
			assinatura.initSign(privateKey);
			assinatura.update(teste);
			byte[] resp = assinatura.sign();

			PublicKey chavePublica = AccessFileFunctions.readDigitalCertificate(((String) user.get("certificate")).getBytes()).getPublicKey();
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

	/* **************************************************************************************************
	 **
	 **  Read Digital Certificate
	 **
	 ****************************************************************************************************/

	public static X509Certificate readDigitalCertificate(byte[] bytes) {
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

	/* **************************************************************************************************
	 **
	 **  Certo To String
	 **
	 ****************************************************************************************************/

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

	/* **************************************************************************************************
	 **
	 **  MARK: Register User
	 **
	 ****************************************************************************************************/

	public static boolean registerUser(Integer grupoId, String senha, String pathCert) {

		byte[] certDigBytes;

		try {
			certDigBytes = FileUtils.readFileToByteArray(new File(pathCert));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		X509Certificate cert = readDigitalCertificate(certDigBytes);

		String subjectDN = cert.getSubjectDN().getName();

		int start = subjectDN.indexOf("=");

		int end = subjectDN.indexOf(",");

		String email = subjectDN.substring(start + 1, end);

		start = subjectDN.indexOf("=", end);
		end = subjectDN.indexOf(",", start);

		String salt = AccessFileFunctions.generateSalt();

		boolean ret = DBControl.getInstance().addUser(subjectDN.substring(start + 1, end), email, grupoId, salt, AccessFileFunctions.generateHashPassword(senha, salt), certToString(cert));

		return ret;
	}

	/* **************************************************************************************************
	 **
	 **  Check E-mail
	 **
	 ****************************************************************************************************/

	public static HashMap checkEmail(String email) {
		List<HashMap> list = null;

		list = DBControl.getInstance().getUser(email);

		if (list != null && list.size() == 1)
			return list.get(0);

		return null;
	}

	/* **************************************************************************************************
	 **
	 **  Check Password
	 **
	 ****************************************************************************************************/

	public static boolean checkPassword(String senha, HashMap user)  {
		String senhaDigest = AccessFileFunctions.generateHashPassword(senha, (String) user.get("salt"));
		if (user.get("passwordDigest").equals(senhaDigest))
			return true;
		return false;
	}

	/* **************************************************************************************************
	 **
	 **  Generate Hash Password
	 **
	 ****************************************************************************************************/

	public static String generateHashPassword(String senha, String salt) {
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

	/* **************************************************************************************************
	 **
	 **  Generate Salt
	 **
	 ****************************************************************************************************/

	private static String generateSalt() {
		SecureRandom rand = new SecureRandom();
		StringBuffer salt = new StringBuffer();
		for (int i = 0; i < 10; i++) {
			salt.append(new Integer(rand.nextInt(9)).toString());
		}
		return salt.toString();
	}

	/* **************************************************************************************************
	 **
	 **  Validate Password
	 **
	 ****************************************************************************************************/

	public static boolean validatePassword(String senha, String confirmacao, HashMap user) {

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
				DBControl.getInstance().insertRegister(MensagemType.PRIMEIRO_ERRO_SENHA_PESSOAL_CONTABILIZADO, LoggedUser.getInstance().getEmail());
				break;
			case 2:
				DBControl.getInstance().insertRegister(MensagemType.SEGUNDO_ERRO_SENHA_PESSOAL_CONTABILIZADO, LoggedUser.getInstance().getEmail());
				break;
			case 3:
				DBControl.getInstance().insertRegister(MensagemType.TERCEIRO_ERRO_SENHA_PESSOAL_CONTABILIZADO, LoggedUser.getInstance().getEmail());
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

	}

	/* **************************************************************************************************
	 **
	 **  Should Block User For Private Key
	 **
	 ****************************************************************************************************/

	public static boolean shouldBlockUserForPrivateKey() {

		HashMap user = LoggedUser.getInstance().getUser();

		int wrongAccess = ((Integer) user.get("numberWrongAccessPrivateKey"));

		return wrongAccess >= 3;

	}


}
