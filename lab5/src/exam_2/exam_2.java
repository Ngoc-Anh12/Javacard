package exam_2;

import javacard.framework.*;
import exam_1.masterInterface;
public class exam_2 extends Applet
{
	final static byte[] serverAID=new byte[]{0x11,0x22,0x33,0x44,0x55,0x00};
	sinhvien sv;
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new exam_2().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
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
			AID exam_1_AID=JCSystem.lookupAID(serverAID,(short)0,(byte)serverAID.length);
			masterInterface sio=(masterInterface)(JCSystem.getAppletShareableInterfaceObject(exam_1_AID,(byte)0x00));
			
			//su dung mang array lam doi so co phuong thuc cua giao dien co the chia se
			// short len=sio.getArray(buf);
			
			//su dung cac kieu du lieu lam tham so va gia tri tra ve
			 sv.toan=(byte)0x09;
			 sv.van=(byte)0x07;
			 sv.diemtb=sio.tinhdiem(sv.toan,sv.van);
			buf[0]=sv.diemtb;
			apdu.setOutgoingAndSend((short)0,(short)1);
			break;
			//tuong lua se ngan khong cho truy cap vao mang nay
			//ket qua la se mot ngoai le duoc tra lai
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
			
		}
	}

	
}
