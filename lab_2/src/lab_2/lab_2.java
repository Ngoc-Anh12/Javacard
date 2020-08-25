package lab_2;

import javacard.framework.*;

public class lab_2 extends Applet
{
	//khai bao cac doi tuong va bien
	private static byte[] buffer,buffer1,buffer2;
	private byte a;
// khai bao ma INS trong APDU
	final static byte INS_SEND =(byte)0x40;
	final static byte INS_UPDATE=(byte)0x01;
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new lab_2().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
		//create bien lien tuc
		buffer =new byte[2];
		//create doi tuong tam thoi
		buffer1=JCSystem.makeTransientByteArray((short)2,JCSystem.CLEAR_ON_DESELECT);
		buffer2=JCSystem.makeTransientByteArray((short)2,JCSystem.CLEAR_ON_RESET);
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
		case INS_SEND:
			//send gia tri cacs doi tuong va bien len may chu
			//dat huong truyen du lieu
			short len=apdu.setOutgoing();
			//thong bao cho may chu so bytes se gui
			apdu.setOutgoingLength((short)7);
			//send data
			buf[0]=a;
			apdu.sendBytes((short)0,(short)1);
			apdu.sendBytesLong(buffer,(short)0,(short)2);
			apdu.sendBytesLong(buffer1,(short)0,(short)2);
			apdu.sendBytesLong(buffer2,(short)0,(short)2);
			break;
		case INS_UPDATE:
			a=9;
			buffer[0]=0x01;
			buffer[1]=0x02;
			buffer1[0]=0x03;
			buffer1[1]=0x04;
			buffer2[0]=0x05;
			buffer2[1]=0x06;
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
