package lab5_1;

import javacard.framework.*;

public class lab5_1 extends Applet implements lab5Interface
{
	
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new lab5_1().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	}

	public void process(APDU apdu)
	{
		if (selectingApplet())
		{
			return;
		}

		byte[] buf = apdu.getBuffer();
		switch (buf[ISO7816.OFFSET_INS])
		{
		case (byte)0x00:
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}
	public Shareable getShareableInterfaceObject(AID client_AID,byte parameter){
		if(parameter!=(byte)0x00){//xac thuc nguoi dung
			return null;
		}
		return this;	
	}
	public byte diemsv(byte toan,byte van){
		byte tb=(byte)(((byte)(toan+van))/2);
		return tb;
	}
}
