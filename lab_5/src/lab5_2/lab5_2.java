package lab5_2;

import javacard.framework.*;
import lab5_1.lab5Interface;
public class lab5_2 extends Applet 
{
	final static byte[]serverAID= new byte[]{0x55,0x66,0x77,0x88,0x99,0x00};
	
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new lab5_2().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
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
			//lay AID co trong file CAP
			AID getAID=JCSystem.lookupAID(serverAID,(short)0,(byte)(serverAID.length));
			lab5Interface lab5_2SIO=(lab5Interface)(JCSystem.getAppletShareableInterfaceObject(getAID,(byte)0x00));
			byte toan=(byte)0x09;
			byte van=(byte)0x07;
			byte tb=lab5_2SIO.diemsv(toan,van);
			buf[0]=tb;
			apdu.sendBytes((short)0,(short)1);
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
