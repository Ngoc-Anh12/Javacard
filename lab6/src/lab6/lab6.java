package lab6;
import javacard.security.*;
import javacardx.crypto.*;
import javacard.framework.*;

public class lab6 extends Applet
{
	private MessageDigest sha;//lop co so cho thuat toan ham bam
	private byte[]m1, m2,m3;
	private lab6(){
		//chi dinh thuat toan can su dung
		//false: doi tuong tom luoc thong diep dc tra lai ko danh cho truy cap ben ngoai
		sha=MessageDigest.getInstance(MessageDigest.ALG_SHA,false);
		m1=new byte[]{0x01,0x02};
		m2=new byte[]{0x03,0x04,0x05};
		m3=new byte[]{0x06,0x07};
		
	}
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new lab6().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
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
		case (byte)0x00:
			//
			sha.update(m1,(short)0,(short)(m1.length));//cung cap toan bo dl trong mang m1
			sha.update(m2,(short)0,(short)(m2.length));
			//gui toan bo dl theo byte  byte m3 nhu dot cuoi cung va luu vao dau mang buf.
			
			short ret=sha.doFinal(m3,(short)0,(short)(m3.length),buf,(short)0);
			//doi tuong tom luoc thong diem tu dong duoc dat lai.
			apdu.setOutgoingAndSend((short)0,ret);
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
