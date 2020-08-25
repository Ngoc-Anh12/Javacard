package la6_2;
import javacard.framework.*;
import javacard.security.*;
import javacardx.crypto.*;
import javacard.security.KeyBuilder;


public class la6_2 extends Applet
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
	
	private la6_2(){
		rsasig=Signature.getInstance(Signature.ALG_RSA_SHA_PKCS1,false);
		rsaPrivKey=(RSAPrivateKey) KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PRIVATE,(short)(8*sigLen),false);
		rsaPublKey=(RSAPublicKey)KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PUBLIC,(short)(8*sigLen),false);
		KeyPair keypair=new KeyPair(KeyPair.ALG_RSA,(short)(8*sigLen));
		keypair.genKeyPair();
		rsaPrivKey=(RSAPrivateKey)keypair.getPrivate();
		rsaPublKey=(RSAPublicKey)keypair.getPublic();
		
		s1=new byte[]{0x01,0x02};
		s2=new byte[]{0x03,0x04,0x05};
		s3=new byte[]{0x06,0x07,0x08};
		sigLen= (short) (KeyBuilder.LENGTH_RSA_1024/8);
		sig_buffer=new byte[sigLen];
		
	}
	
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new la6_2().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
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
		rsasig.init(rsaPrivKey,Signature.MODE_SIGN);
		rsasig.update(s1,(short)0,(short)(s1.length));
		rsasig.update(s2,(short)0,(short)(s2.length));
		rsasig.sign(s3,(short)0,(short)(s3.length),sig_buffer,(short)0);
		apdu.setOutgoing();
		apdu.setOutgoingLength(sigLen);
		apdu.sendBytesLong(sig_buffer,(short)0,sigLen);
		
	}
	private void rsaverify(APDU apdu){
		byte []buf=apdu.getBuffer();
		rsasig.init(rsaPublKey,Signature.MODE_VERIFY);
		rsasig.update(s1,(short)0,(short)(s1.length));
		rsasig.update(s2,(short)0,(short)(s2.length));
		boolean ret=rsasig.verify(s3,(short)0,(short)(s3.length),sig_buffer,(short)0,sigLen);
		buf[(short)0]=ret?(byte)1:(byte)0;
		apdu.setOutgoingAndSend((short)0,(short)1);
	}
}
