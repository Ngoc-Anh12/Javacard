package lab6_4;

import javacard.framework.*;

public class lab6_4 extends Applet
{
	private static final byte INS_MSV =(byte)0x00;
	private static final byte INS_NHAP =(byte)0x01;
	private static final byte INS_IN =(byte)0x02;
	private static final byte INS_SUA =(byte)0x03;
	private static final byte INS_XOA =(byte)0x04;
	
	private static byte sv_id_len =(byte)0x08;
	private static byte[]sinhvien;
	private static byte somon;
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new lab6_4(bArray,(bOffset),bLength);
	}
	private lab6_4(byte[] bArray, short bOffset, byte bLength){
		byte bLi=bArray[bOffset];
		if(bLi==0){
			register();
		}
		else{
			register(bArray,(short)(bOffset+1),bLi);
			
		}
		bOffset=(short)(bOffset+bLi+1);
		byte bLc=bArray[bOffset];
		bOffset=(short)(bOffset+bLc+1);
		byte bLa=bArray[bOffset];
		bOffset=(short)(bOffset+1);
		if(bLa!=0){
			sinhvien=new byte[sv_id_len];
			Util.arrayCopy(bArray,bOffset,sinhvien,(byte)0,sv_id_len);
			bOffset+=sv_id_len;
			somon=bArray[bOffset];
			
		}
		else{
			sinhvien=new byte[]{'C','T','0','1','0','1','0','3'};
			somon=(byte)0x04;
		}
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
		case INS_MSV:
			apdu.setOutgoing();
			apdu.setOutgoingLength((short)9);
			apdu.sendBytesLong(sinhvien,(short)0,sv_id_len);
			buf[0]=somon;
			apdu.sendBytes((short)0,(short)1);
			break;
		case INS_NHAP:
			break;
		case INS_IN:
			break;
		case INS_SUA:
			break;
		case INS_XOA:
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
