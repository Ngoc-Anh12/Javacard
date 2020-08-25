package lab6_5;

import javacard.framework.*;
import javacard.security.*;


public class lab6_5 extends Applet
{
	private byte[] seed;
	private RandomData randata;
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new lab6_5().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
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
			seed=new byte[]{0x01,0x02,0x03};
			randata= RandomData.getInstance(RandomData.ALG_PSEUDO_RANDOM);
			//cung cap mam ngau nhie
			randata.setSeed(seed,(short)0,(short)(seed.length));
			short randomlen=(short)15;
			//sinh du lieu ngau nhien
			randata.generateData(buf,(short)0,randomlen);
			apdu.setOutgoingAndSend((short)0,randomlen);
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
