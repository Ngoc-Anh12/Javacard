package lab6_3;

import javacard.framework.*;
import javacard.security.*;
import javacardx.crypto.Cipher;
import javacard.security.KeyBuilder;
public class lab6_3 extends Applet
{
	private static final byte INS_ENCRYPT=(byte)0x01;
	private static final byte INS_DENCRYPT=(byte)0x02;
	
	private static byte[]in,key,e_crypt,d_crypt;
	private short keylen;
	private AESKey aeskey;
	private Cipher cipher;
	
	private lab6_3(){
		keylen=(short)(KeyBuilder.LENGTH_AES_128/8);
		in=new byte[keylen];
		key=new byte[keylen];
		e_crypt=new byte [keylen];
		d_crypt=new byte [keylen];
		for(byte i=0;i<(short)keylen;i++){
			key[i]=(byte)i;
			in[i]=(byte)(i+1);
		}
		//khoi tao cipher
		cipher=Cipher.getInstance(Cipher.ALG_AES_BLOCK_128_CBC_NOPAD,false);
		aeskey=(AESKey)KeyBuilder.buildKey(KeyBuilder.TYPE_AES,(short)(8*keylen),false);
		aeskey.setKey(key,(short)0);
	}
	
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new lab6_3().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
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
		case INS_ENCRYPT:
			ENCRYPT(apdu);
			break;
		case INS_DENCRYPT:
			DENCRYPT(apdu);
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}
	private void ENCRYPT(APDU apdu){
		byte buf= apdu.getBuffer();
		
		cipher.init(aeskey,Cipher.MODE_ENCRYPT);
		cipher.doFinal(in,(short)0,keylen,e_crypt,(short)0);
		Util.arrayCopy(e_crypt,(short)0,buf,(short)0,keylen);
		apdu.setOutgoingAndSend((short)0,keylen);
	}
	private void DENCRYPT(APDU apdu){
		byte buf= apdu.getBuffer();
		
		cipher.init(aeskey,Cipher.MODE_DECRYPT);
		cipher.doFinal(e_crypt,(short)0,keylen,d_crypt,(short)0);
		Util.arrayCopy(d_crypt,(short)0,buf,(short)0,keylen);
		apdu.setOutgoingAndSend((short)0,keylen);
	}
}
