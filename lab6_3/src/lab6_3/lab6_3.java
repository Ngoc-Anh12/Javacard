package lab6_3;

import javacard.framework.*;
import javacard.security.*;
import javacardx.crypto.Cipher;
public class lab6_3 extends Applet
{
	private static final byte INS_ENCRYPT =(byte)0x00;
	private static final byte INS_DECRYPT =(byte)0x01;
	private byte[]in, enc_buf,de_buf,key;
	
	private AESKey asekey;
	private Cipher cipher;
	private short keylen;
	private lab6_3(){
		keylen =(short)(KeyBuilder.LENGTH_AES_128/8);
		in= new byte[keylen];
		enc_buf=new byte[keylen];
		de_buf=new byte[keylen];
		key=new byte[keylen];
		
		for(byte i=0;i<(byte)keylen;i++){
			key[i]=(byte)i;
			in[i]=(byte)(i+1);
		}
		
		cipher=Cipher.getInstance(Cipher.ALG_AES_BLOCK_128_ECB_NOPAD,false);
		asekey=(AESKey)KeyBuilder.buildKey(KeyBuilder.TYPE_AES,(short)(8*keylen),false);
		asekey.setKey(key,(short)0);
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
			encrypt(apdu);
			break;
		case INS_DECRYPT:
			decrypt(apdu);
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}
	private void encrypt(APDU apdu){
		byte[] buf=apdu.getBuffer();
		cipher.init(asekey,Cipher.MODE_ENCRYPT);
		cipher.doFinal(in,(short)0,keylen,enc_buf,(short)0);
		Util.arrayCopy(enc_buf,(short)0,buf,(short)0,keylen);
		apdu.setOutgoingAndSend((short)0,keylen);
		

}
	private void decrypt(APDU apdu){
		byte[]buf=apdu.getBuffer();
		cipher.init(asekey,Cipher.MODE_DECRYPT);
		cipher.doFinal(enc_buf,(short)0,keylen,de_buf,(short)0);
		Util.arrayCopy(de_buf,(short)0,buf,(short)0,keylen);
		apdu.setOutgoingAndSend((short)0,keylen);
		
		
	}
}
