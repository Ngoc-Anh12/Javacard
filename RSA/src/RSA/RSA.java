package RSA;
import javacard.framework.*;
import javacard.security.*;
import javacardx.crypto.*;
import javacard.security.KeyBuilder;

public class RSA extends Applet
{
	private static final byte INS_SIGN= (byte) 0x01;
	private static final byte INS_VERIFY= (byte) 0x02;
	
	private RSAPrivateKey rsaPrivKey;
	private RSAPublicKey rsaPublKey;
	
	//tuong tu lop messagedigest
	private Signature rsasig;//ho tro tap hop rong lon cac thuat toan chu ki
	//khai bao mang
	private byte[] s1,s2,s3,sig_buffer;
	private short sigLen;
	
	private RSA(){
		s1=new byte[]{0x01,0x02};
		s2=new byte[]{0x03,0x04,0x05};
		s3=new byte[]{0x06,0x07};
		sigLen= (short) (KeyBuilder.LENGTH_RSA_1024/8);//do dai khoa
		sig_buffer=new byte[sigLen];
		//tao doi tuong signature
		rsasig=Signature.getInstance(Signature.ALG_RSA_SHA_PKCS1,false);
		//buildkey tra ve mot doi tuong khoa voi loai khoa dc yeu cau
		rsaPrivKey=(RSAPrivateKey) KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PRIVATE,(short)(8*sigLen),false);
		rsaPublKey=(RSAPublicKey)KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PUBLIC,(short)(8*sigLen),false);
		//khoi tao khoa
		KeyPair keypair=new KeyPair(KeyPair.ALG_RSA,(short)(8*sigLen));
		keypair.genKeyPair();
		//khoi tao khoa cong khai va bi mat
		rsaPrivKey=(RSAPrivateKey)keypair.getPrivate();
		rsaPublKey=(RSAPublicKey)keypair.getPublic();		
			
	}
	
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new RSA().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
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
			rsasig(apdu);
			break;
		case INS_VERIFY:
			rsaverify(apdu);
			break;	
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}
	private void rsasig(APDU apdu){
		//ki chu ky-bieu thi che do ki
		rsasig.init(rsaPrivKey,Signature.MODE_SIGN);
		//cung cap du lieu
		rsasig.update(s1,(short)0,(short)(s1.length));
		rsasig.update(s2,(short)0,(short)(s2.length));
		//cung cap lo du lieu cuoi su dung sign
		rsasig.sign(s3,(short)0,(short)(s3.length),sig_buffer,(short)0);
		apdu.setOutgoing();
		apdu.setOutgoingLength(sigLen);
		apdu.sendBytesLong(sig_buffer,(short)0,sigLen);
		
	}
	private void rsaverify(APDU apdu){
		byte []buf=apdu.getBuffer();
		
		rsasig.init(rsaPublKey,Signature.MODE_VERIFY);//bieu thi che do xac minh
		rsasig.update(s1,(short)0,(short)(s1.length));
		rsasig.update(s2,(short)0,(short)(s2.length));
		//verify xac minh chu ki tu du lieu dau vao so voi du lieu cung cap
		boolean ret=rsasig.verify(s3,(short)0,(short)(s3.length),sig_buffer,(short)0,sigLen);
		buf[(short)0]=ret?(byte)1:(byte)0;
		apdu.setOutgoingAndSend((short)0,(short)1);
	}
}
