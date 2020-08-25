package giuaki;

import javacard.framework.*;

public class giuaki extends Applet

{	
	private static final byte INS_NHAP =(byte) 0x00;
	private static final byte INS_IN =(byte) 0x01;
	// private static final byte INS_SUA =(byte) 0x02;
	// private static final byte INS_XOA =(byte) 0x03;
	final static byte chuthe_id_length=(byte)0x03;

	private static byte[]chuthe;
	private static byte diem;
	// private static short datalen,index,i,len,diemlen;
	
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new giuaki(bArray,(bOffset),bLength);
	}
private giuaki(byte[] bArray,short bOffset,byte Length){
	
	//kiem tra du lieu den
	byte ilen=bArray[bOffset];//do dai AID
	if(ilen==0){
		//dang ky applet voi JCRE su dung AID mac dinh
		register();
	}
	else{
		//dang ky applet voi JCRE su dung AID duoc chi dinh trong tham so
		register(bArray,(short)(bOffset+1),ilen);	
	}
	bOffset=(short)(bOffset+ilen+1);
	byte clen=bArray[bOffset];//do dai thong tin dieu khien
	bOffset=(short)(bOffset+clen+1);
	byte alen=bArray[bOffset];
	bOffset=(short)(bOffset+1);
	//doc du lieu applet
	if(alen!=0){
		
		//gan id cua chu the
		chuthe=new byte[chuthe_id_length];
		Util.arrayCopy(bArray,bOffset,chuthe,(byte)0,chuthe_id_length);
		bOffset+=chuthe_id_length;
		
		//gan so diem
		diem=bArray[bOffset];
	}
	else{
		//gan id chu the
		chuthe=new byte[]{'0','1','0'};	
		diem=(byte)0x09;
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
		case INS_NHAP:
			apdu.setOutgoing();
			apdu.setOutgoingLength((short)3);
			apdu.sendBytesLong(chuthe,(short)0,chuthe_id_length);
			break;
		case INS_IN:
			apdu.setOutgoing();
			apdu.setOutgoingLength((short)1);
			buf[0]=diem;
			apdu.sendBytes((short)0,(short)1);
			break;
		
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
