package lab_3_vidu2;

import javacard.framework.*;

public class lab_3_vidu2 extends Applet
{

	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new lab_3_vidu2().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
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
			byte[] buf1 =new byte[3];
			//tao doi tuong tam thoi
			//clear_on_reset duy tri du lieu can luu tru tren applet
			byte[]buf2 = JCSystem.makeTransientByteArray((short)3,JCSystem.CLEAR_ON_RESET);
			//bat dau giao dich
			JCSystem.beginTransaction();
			for(byte i=0;i<3;i++){
				buf1[i]=(byte)(i+1);
				buf2[i]=(byte)(2*(byte)(i+1));
			}	
			byte a=1;
			//huy giao dich
			JCSystem.abortTransaction();
			apdu.setOutgoing();
			apdu.setOutgoingLength((short)7);
			apdu.sendBytesLong(buf1,(short)0,(short)3);
			apdu.sendBytesLong(buf2,(short)0,(short)3);
			//dien cac phan tu cua mot mang mot cach khong nguyen tu voi gia tri chi dinh
			Util.arrayFillNonAtomic(buf,(short)0,(short)1,a);//a la bien cuc bo
			apdu.sendBytes((short)0,(short)1);
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
