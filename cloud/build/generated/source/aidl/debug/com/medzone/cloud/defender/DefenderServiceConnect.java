/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\app\\cloud\\src\\main\\aidl\\com\\medzone\\cloud\\defender\\DefenderServiceConnect.aidl
 */
package com.medzone.cloud.defender;
public interface DefenderServiceConnect extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.medzone.cloud.defender.DefenderServiceConnect
{
private static final java.lang.String DESCRIPTOR = "com.medzone.cloud.defender.DefenderServiceConnect";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.medzone.cloud.defender.DefenderServiceConnect interface,
 * generating a proxy if needed.
 */
public static com.medzone.cloud.defender.DefenderServiceConnect asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.medzone.cloud.defender.DefenderServiceConnect))) {
return ((com.medzone.cloud.defender.DefenderServiceConnect)iin);
}
return new com.medzone.cloud.defender.DefenderServiceConnect.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_initJPush:
{
data.enforceInterface(DESCRIPTOR);
this.initJPush();
reply.writeNoException();
return true;
}
case TRANSACTION_startJPush:
{
data.enforceInterface(DESCRIPTOR);
this.startJPush();
reply.writeNoException();
return true;
}
case TRANSACTION_stopJPush:
{
data.enforceInterface(DESCRIPTOR);
this.stopJPush();
reply.writeNoException();
return true;
}
case TRANSACTION_getRegisterID:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getRegisterID();
reply.writeNoException();
reply.writeString(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.medzone.cloud.defender.DefenderServiceConnect
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void initJPush() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_initJPush, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void startJPush() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startJPush, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void stopJPush() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopJPush, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String getRegisterID() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getRegisterID, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_initJPush = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_startJPush = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_stopJPush = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_getRegisterID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
public void initJPush() throws android.os.RemoteException;
public void startJPush() throws android.os.RemoteException;
public void stopJPush() throws android.os.RemoteException;
public java.lang.String getRegisterID() throws android.os.RemoteException;
}
