package lab2;

import javacard.framework.*;

public class lab2 extends Applet
{
	private static final byte INS_SEND=(byte)0x01;
	private static final byte INS_UPDATE=(byte)0x02;
	private static final byte INS_COPY=(byte)0x03;
	private static byte[]buffer,buffer1,buffer2;
	private byte x;
	
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new lab2().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
		//khai bao bien lien tuc
		 buffer = new byte[3];
		//khai bao bien tam thoi
		 buffer1=JCSystem.makeTransientByteArray((short)3,JCSystem.CLEAR_ON_DESELECT);
		 buffer2=JCSystem.makeTransientByteArray((short)3,JCSystem.CLEAR_ON_RESET);
		
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
			
			short data=apdu.setOutgoing();
			apdu.setOutgoingLength((short)10);
			//gui du lieu
			buf[0]=x;
			apdu.sendBytes((short)0,(short)1);
			apdu.sendBytesLong(buffer,(short)0,(short)3);
			apdu.sendBytesLong(buffer1,(short)0,(short)3);
			apdu.sendBytesLong(buffer2,(short)0,(short)3);
			break;
		case INS_UPDATE:
			x=3;
			buffer[0]=0x01;
			buffer[1]=0x02;
			buffer[2]=0x03;
			
			buffer1[0]=0x01;
			buffer1[1]=0x02;
			buffer1[2]=0x03;
			
			buffer2[0]=0x01;
			buffer2[1]=0x02;
			buffer2[2]=0x03;
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
