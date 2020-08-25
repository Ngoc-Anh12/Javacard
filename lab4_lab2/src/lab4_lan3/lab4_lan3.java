package lab4_lan3;

import javacard.framework.*;

public class lab4_lan3 extends Applet
{
	final static byte SV_ID_LENGTH = (byte)0x04;
	final static byte INS_INF = (byte)0x00;
	final static byte INS_NHAP = (byte)0x01;
	final static byte INS_IN = (byte)0x02;
	final static byte INS_SUA = (byte)0x03;
	final static byte INS_XOA = (byte)0x04;

	private static byte[] diemThi, sinhVien;
	private static byte soLuongMonThi, id, diem;
	private static short dataLen, diemLen, len, index;
	
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new lab4_lan3(bArray, bOffset, bLength);
	}

	private lab4_lan3(byte[] bArray, short bOffset, byte bLength)
	{
		byte iLen = bArray[bOffset];
		if(iLen == 0){
			register();
		} else {
			register(bArray, (short) (bOffset + 1), iLen);
		}
		bOffset = (short) (bOffset+iLen+1);
		byte cLen = bArray[bOffset];
		bOffset = (short) (bOffset+cLen+1);
		byte aLen = bArray[bOffset];
		bOffset = (short) (bOffset+1);
		if(aLen != 0){
			sinhVien = new byte[SV_ID_LENGTH];
			Util.arrayCopy(bArray, bOffset, sinhVien, (byte)0, SV_ID_LENGTH);
			bOffset += SV_ID_LENGTH;
			soLuongMonThi = bArray[bOffset];
		} else {
			sinhVien = new byte[] {'S', 'V', '0', '1'};
			soLuongMonThi = (byte)0x09;
		}
		len = (short)(2*(short)(soLuongMonThi));
		diemThi = new byte[len];
	}
	public void process(APDU apdu)
	{
		if (selectingApplet())
		{
			return;
		}

		byte[] buf = apdu.getBuffer();
		apdu.setIncomingAndReceive();
		id = (byte) buf[ISO7816.OFFSET_P1];
		diem = (byte) buf[ISO7816.OFFSET_P2];
		dataLen = (short) (buf[ISO7816.OFFSET_LC]&0xff);
		switch (buf[ISO7816.OFFSET_INS])
		{
		case INS_INF:
			apdu.setOutgoing();
			apdu.setOutgoingLength((short)5);
			apdu.sendBytesLong(sinhVien, (short)0, SV_ID_LENGTH);
			buf[0] = soLuongMonThi;
			apdu.sendBytes((short)0, (short)1);
			break;
		case INS_NHAP:
			index = (short)(ISO7816.OFFSET_CDATA);
			// check dieu kien so diem thi nhap vao khong duoc nhap qua so mon thi
			if(dataLen <= len)
			{
				//check dieu kien khong trung ID
				short got = 0;
				for(short i = index; i < (short)(index + (short)(dataLen - 3)); i += 2)
				{
					for(short k = (short)(i + 2); k < (short)(index + (short)(dataLen - 1)); k += 2)
					{
						if(buf[i] == buf[k])
						{
							got = 1;
							break;
						}
					}
				}
				if(got == 0)//neu khong trung thi insert du lieu
				{
					short j = (short)0;
					for(short i = index; i < (short)(index + dataLen); i++)
					{
						diemThi[j] = buf[i];
						j++;
					}
					diemLen = j;
				}
				else
				{
					ISOException.throwIt(ISO7816.SW_WRONG_DATA);
				}
			}
			else
			{
				ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
			}
			apdu.setOutgoing();
			apdu.setOutgoingLength(len);
			apdu.sendBytesLong(diemThi, (short)0, len);
			break;
		case INS_IN:
			apdu.setOutgoing();
			apdu.setOutgoingLength(len);
			apdu.sendBytesLong(diemThi, (short)0, len);
			break;
		case INS_SUA:
			index = (short) (-1);
			//tim vi tri ID mon can sua
			for(short i = 0; i < (short)(diemLen - 1); i += 2)
			{
				if(id == diemThi[i])
				{
					index = i;
					break;
				}
			}
			if(index != -1){//neu tim duoc thi sua du lieu
				diemThi[(short)(index + 1)] = diem;
			}
			else
			{
				ISOException.throwIt(ISO7816.SW_DATA_INVALID);
			}
			apdu.setOutgoing();
			apdu.setOutgoingLength(len);
			apdu.sendBytesLong(diemThi, (short)0, len);
			break;
		case INS_XOA:
			index = (short) (-1);
			//tim ID mon hoc can xoa
			for(short i = 0; i < (short)(diemLen - 1); i += 2)
			{
				if(id == diemThi[i])
				{
					index = i;
					break;
				}
			}
			if(index != -1){//neu tim duoc thi tien hanh xoa
				if(index == (short)(diemLen - 2))//neu la phan tu cuoi cua mang thi xoa luon
				{
					diemThi[index] = (byte)0x00;
					diemThi[(short)(index + 1)] = (byte)0x00;
				}
				else // neu khong phai phan tu cuoi thi tien hanh xoa va don du lieu lai
				{
					for(short k = index; k < (short)(diemLen - 2); k++)
					{
						diemThi[k] = diemThi[(short)(k + 2)];
						diemThi[(short)(k + 2)] = (byte)0x00;
					}
				}
				diemLen -= 2;
			}
			else
			{
				ISOException.throwIt(ISO7816.SW_DATA_INVALID);
			}
			apdu.setOutgoing();
			apdu.setOutgoingLength(len);
			apdu.sendBytesLong(diemThi, (short)0, len);
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
