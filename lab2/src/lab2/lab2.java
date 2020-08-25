package lab2;

import javacard.framework.*;

public class lab2 extends Applet
{

	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new lab2().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
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
		case (byte)0x40:
			
			byte[] name={'a','n','h'};
			byte[]date={01,02,98};
			short name_len=(short)name.length;
			short date_len=(short)date.length;
			short all_len=(short)(name_len+date_len);
			
			
			
			///dat huong truyen du lieu
			short data=apdu.setOutgoing();
			//thong bao may chu ve so byte thuc te
			apdu.setOutgoingLength(all_len);
			//gui du lieu
			Util.arrayCopy(name,(short)0,buf,(short)0,name_len);
			apdu.sendBytes((short)0,name_len);
			
			Util.arrayCopy(date,(short)0,buf,(short)0,date_len);
			apdu.sendBytes((short)0,date_len);			
			
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
