package com.xyy.game.ANN;

import java.lang.Math;

/**
 * ��Ԫ��
 * 
 * @author Xyy
 *
 */

public class Neuron {
	//��Ԫ����������
	public final int m_iNumInputs;

	//��ÿ�������Ȩ��
	public double[] m_aryWeight;

	//����Ԫ�ļ���ֵ
	public double m_dActivation;

	//���ֵ
	public double m_dError;

	//���캯��
	public Neuron(int NumInputs) {
		//������Ҫһ�������Ȩ��������ƫ����(bias),��� +1
		m_iNumInputs = NumInputs+1;
		m_aryWeight = new double[m_iNumInputs];
		m_dActivation = 0;
		m_dError = 0;
		for (int i=0; i<m_iNumInputs; ++i)
		{
			//��ʼ��Ȩ��Ϊһ�������(-1 ~ 1)
			m_aryWeight[i] = Math.random()*2 -1;
		}
	}
}
