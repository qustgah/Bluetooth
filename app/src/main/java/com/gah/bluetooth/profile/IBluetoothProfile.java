package com.gah.bluetooth.profile;

import android.bluetooth.BluetoothDevice;

interface IBluetoothProfile {

	/** 优先级 */
	int PRIORITY_OFF = 0;
	int PRIORITY_ON = 100;

	/**
	 * 是否可用的
	 */
	boolean isAvailable();

	/**
	 * 连接设备
	 * @param device 设备
	 */
	boolean connect(BluetoothDevice device);

	/**
	 * 断开设备
	 * @param device 设备
	 */
	boolean disconnect(BluetoothDevice device);


	/**
	 * 设置设备优先权
	 * @param device   设备
	 * @param priority 优先权
	 */
	boolean setPriority(BluetoothDevice device, int priority);

	/**
	 * 获取设备优先权
	 * @param device 设备
	 * @return 优先权
	 */
	int getPriority(BluetoothDevice device);

	/**
	 * 获取设备是否拥有优先权
	 * @param device 设备
	 */
	boolean isPreferred(BluetoothDevice device);

	/**
	 * 关闭该profile服务
	 */
	void close();
}
