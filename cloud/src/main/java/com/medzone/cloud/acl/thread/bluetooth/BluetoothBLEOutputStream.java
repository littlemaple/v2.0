package com.medzone.cloud.acl.thread.bluetooth;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

public class BluetoothBLEOutputStream extends OutputStream{
	public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
	private BluetoothGattCharacteristic mNotifier;
	private BluetoothGattCharacteristic mReader;
	private BluetoothGattCharacteristic mWriter;
	private BluetoothGatt               mBluetoothGatt;
	BluetoothBLEOutputStream(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic writer, BluetoothGattCharacteristic reader)
	{
		mBluetoothGatt = bluetoothGatt;
		mWriter = writer;
		mReader = reader;
	}
	
	
	@Override
	public void close(){
		mBluetoothGatt = null;
		mWriter = null;
	}
	
	@Override
	public void write(int oneByte) throws IOException {
		byte[] content = new byte[1];
		content[0] = (byte) oneByte;
		write(content);
	}
	
	@Override
	public void write(byte[] buffer) throws IOException  {
		if (mWriter == null || mBluetoothGatt == null)
			throw new IOException("Gatt or writer is null");
		
		mWriter.setValue(buffer);
		mBluetoothGatt.writeCharacteristic(mWriter);
		
		if (mNotifier == null)
		{
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			fetch();
		}
	}
	
	private void fetch()
	{
		final int charaProp = mReader.getProperties();
		if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == BluetoothGattCharacteristic.PROPERTY_NOTIFY) {
			mNotifier = mReader;
			setCharacteristicNotification(mReader, true);
		}
	}
	

	@SuppressLint("InlinedApi")
	public void setCharacteristicNotification(
			BluetoothGattCharacteristic characteristic, boolean enabled) {
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
		BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID
				.fromString(CLIENT_CHARACTERISTIC_CONFIG));
		descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		mBluetoothGatt.writeDescriptor(descriptor);
	}

}
