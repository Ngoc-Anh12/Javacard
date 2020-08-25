package lab_3 ;

import javacard.framework.*;

public class lab_3 extends Applet
{
	//khai bao ma INS
	final static byte INS_TRANSACTION = (byte) 0x00;
	final static byte INS_SEND =(byte) 0x01;
	//khai bao doi tuong va bien
	private static byte[] buf1,buf2;
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new lab_3().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
		buf1=new byte[10];
		buf2=new byte[10];
		for(short i=0;i<10;i++)
			buf1[i]=(byte)i;
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
		case INS_TRANSACTION:
			JCSystem.beginTransaction();
			for(short i=0;i<10;i++)
			{
				buf2[i]=buf1[1];
				if(i==(short)5){
					JCSystem.abortTransaction();
					JCSystem.beginTransaction();//bat dau mot giao dich moi voi i>5
				}
			}
			JCSystem.commitTransaction();
			break;
		case INS_SEND:
			apdu.setOutgoing();
			apdu.setOutgoingLength((short)10);
			apdu.sendBytesLong(buf2,(short)0,(short)10);
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
