package helloword;

import javacard.framework.*;

public class helloword extends Applet
{

	public static void install(byte[] bArray, short bOffset, byte bLength) 
	
	{
		new helloword().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	}

	public void process(APDU apdu)
	{
		if (selectingApplet())
		{
			return;
		}

		byte[] buf = apdu.getBuffer();//tham chieu den bo dem apdu vao bien buf
		switch (buf[ISO7816.OFFSET_INS])//bo chuyen doi duoc thu hien tuy thuoc vao gia tri byte ÍN cua len APDU.
										//tat ca byte khac duoc bo qua
		{
		case (byte)0x00://neu INS=00h, noi dung mang hello duoc copy vao APDU sau do duoc gui duoi dang data response
			byte []hel={'h','e','l','l','o'};
			short len=(short)hel.length;
			Util.arrayCopy(hel,(short)0,buf,(short)0,len);
			apdu.setOutgoingAndSend((short)0,len);//data chi gui mot lan
		break;
			//response status cua ngoai le la 6D00
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
