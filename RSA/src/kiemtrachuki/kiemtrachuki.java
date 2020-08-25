package kiemtrachuki;

import javacard.framework.*;
import javacard.security.*;
import javacardx.crypto.*;
import javacard.security.KeyBuilder;
public class kiemtrachuki extends Applet
{
	private static final byte INS_SIGN=(byte)0x01;
	private static final byte INS_VERIFY=(byte)0x02;
	
	private RSAPrivateKey RSAprikey;
	private RSAPublicKey RSApubkey;
	private Signature signrsa;
	private static byte[] s1,s2,s3,sign_buf;
	private short signlen;
	
	private kiemtrachuki(){
		//khoi tao khoa
		s1=new byte[]{0x02,0x03,0x04};
		s2=new byte[]{0x05,0x06};
		s3=new byte[]{0x07,0x08};
		signlen=(short)(KeyBuilder.LENGTH_RSA_1024/8);
		sign_buf=new byte[signlen];
		
		//tao  doi tuong signature
		signrsa=Signature.getInstance(Signature.ALG_RSA_SHA_PKCS1,false);
		
		RSAprikey=(RSAPrivateKey)KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PRIVATE,(short)(8*signlen),false);
		RSApubkey=(RSAPublicKey)KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PUBLIC,(short)(8*signlen),false);
		//khoi tao khoa
		KeyPair keypair=new KeyPair(KeyPair.ALG_RSA,(short)(8*signlen));
		keypair.genKeyPair();
		//khoi tao khoa cong khai va bi mat
		RSAprikey=(RSAPrivateKey)keypair.getPrivate();
		RSApubkey=(RSAPublicKey)keypair.getPublic();
	}
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new kiemtrachuki().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	}

	public void process(APDU apdu)
	{
		if (selectingApplet())
		{
			return;
		}

		byte[] buf = apdu.getBuffer();
		apdu.setIncomingAndReceive();
		switch (buf[ISO7816.OFFSET_INS])
		{
		case INS_SIGN:
			sign(apdu);
			break;
		case INS_VERIFY:
			verify(apdu);
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}
	private  void sign (APDU apdu){
		//bieu thi che do ki
		signrsa.init(RSAprikey,Signature.MODE_SIGN);
		//cung cap du lieu
		signrsa.update(s1,(short)0,(short)(s1.length));
		signrsa.update(s2,(short)0,(short)(s2.length));
		//du lieu cuoi cung su dung sign
		signrsa.sign(s3,(short)0,(short)(s3.length),sign_buf,(short)0);
		apdu.setOutgoing();
		apdu.setOutgoingLength(signlen);
		apdu.sendBytesLong(sign_buf,(short)0,signlen);
	} 
	private void verify(APDU apdu){
		byte []buf=apdu.getBuffer();
		//xac minh chu ki
		signrsa.init(RSApubkey,Signature.MODE_VERIFY);
		signrsa.update(s1,(short)0,(short)(s1.length));
		signrsa.update(s2,(short)0,(short)(s2.length));
		boolean ret=signrsa.verify(s3,(short)0,(short)(s3.length),sign_buf,(short)0,signlen);
		buf[(short)0]= ret?(byte)1:(byte)0;
		apdu.setOutgoingAndSend((short)0,(short)1);
		
		
	}
}
