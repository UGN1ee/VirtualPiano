import java.awt.Color;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Instrument;

class  Piano extends Thread
{
	JFrame f;
	JLayeredPane j;
	String[] p;
	JButton[] b;
	int[] s;
	int y;
	int o;

	private Synthesizer syn;
	private MidiChannel[] m_ch;
	private Instrument[] inst;
	private int inst_n = 0;

	private JLabel inst_L = new JLabel("instrument: ");
	private JLabel inst_lb = new JLabel("Piano 1");
	private JLabel o_lb = new JLabel("옥타브: 0");

	public Piano(JFrame _f, JLayeredPane _j, String[] _p, JButton[] _b, int[] _s, int _y, int _o){
		f = _f; //frame
		j = _j; //layeredpane
		p = _p; //피아노 key배열
		b = _b; //피아노 버튼
		s = _s; //키보드 고유번호 배열
		y = _y; //키보드 위치(위아래 조정)
		o = _o; //옥타브 조정 위한 변수

		/////////////
		// 피아노 틀 //
		/////////////
		int temp = 0; // 건반 for문 위한 변수
		for (int i=0; i<19; i++){ // 건반 버튼 추가
			if (i==0 || i%2==0)	{ //흰건반 추가
				j.add(b[i-temp] = new JButton(p[i-temp]), new Integer(0));
				b[i-temp].setVerticalAlignment(SwingConstants.BOTTOM);
				b[i-temp].setBackground(Color.white);
				b[i-temp].setBounds(200+((i/2)*60), 20+y, 60, 250);

			} else if (i%2==1)  { //검은건반 추가
				j.add(b[i-temp] = new JButton(p[i-temp]), new Integer(100));
				b[i-temp].setBackground(Color.black);
				b[i-temp].setForeground(Color.white);
				b[i-temp].setVerticalAlignment(SwingConstants.BOTTOM);

				if (i==5||i==13){
					temp++;
					continue;
				}else  {
					b[i-temp].setBounds(240+((i/2)*60),20+y,42,160);
				}
			}
		}
	}

	public void run(){
		/////////////
		//   midi  //
		/////////////

		try{	//사운드 생성하는 신디사이저 오픈
			syn = MidiSystem.getSynthesizer();
			syn.open();
		} catch (MidiUnavailableException p) {
			p.printStackTrace();
			System.exit(1);
		}

		this.m_ch = syn.getChannels();
		Soundbank sb = syn.getDefaultSoundbank();
		syn.loadAllInstruments(sb); //음색 가져오기

		this.inst = syn.getAvailableInstruments();
		syn.loadAllInstruments(syn.getDefaultSoundbank());
		syn.getChannels()[0].programChange(inst_n);

		
		inst_L.setBounds(10,30,70,10);
		inst_lb.setBounds(10,50,150,10);
		o_lb.setBounds(10,70,70,10);

		f.add(inst_L);
		f.add(inst_lb);
		f.add(o_lb);
		f.addKeyListener(new key());
		f.setFocusable(true);
		f.requestFocus();
		f.setSize(1000,600);
		f.setVisible(true);
	}



	/////////////////
	// KeyListener //
	/////////////////
	private int oct = 0;
	class key implements KeyListener{
		public void keyPressed(KeyEvent e){
				int prs = e.getExtendedKeyCode();
				int numbr = -1; //무슨 음인지 알려주는 변수

				for (int j=0; j<17; j++) { //눌렀을 때 효과와 사운드 추가
					if (s[j] == prs)	{
						b[j].setBackground(Color.gray);
						numbr = o+j;
					}
				}

				if (prs == KeyEvent.VK_RIGHT)	{ //음색변경
					if (inst_n == inst.length - 1)	{
						inst_n = 0;
					}else{
						inst_n++;
						inst_lb.setText(inst[inst_n].getName());
					}
					syn.getChannels()[0].programChange(inst_n);
				}else if (prs == KeyEvent.VK_LEFT)	{ //음색변경
					if (inst_n == 0)	{
						inst_n = inst.length - 1;
					}else{
						inst_n--;
						inst_lb.setText(inst[inst_n].getName());
					}
				}else if (prs == KeyEvent.VK_UP)	{ // 한 옥타브 올리기
					o += 12;
					oct++;
					o_lb.setText("옥타브: "+oct);
				}else if (prs == KeyEvent.VK_DOWN)	{ // 한 옥타브 내리기
					o -= 12;
					oct--;
					o_lb.setText("옥타브: "+oct);
				}


				if (numbr != -1)	{ //사운드 재생
					m_ch[0].noteOn(numbr, 700);
				}
			}
		public void keyReleased(KeyEvent e){
			//m_ch[0].noteOff(numbr, 700);
			int prs = e.getExtendedKeyCode();
			int numbr = -1; 

			for (int j=0; j<17; j++) {
					if (s[j] == prs)	{
						if (j==1||j==3||j==6||j==8||j==10||j==13||j==15){
							b[j].setBackground(Color.black);
						}else{
							b[j].setBackground(Color.white);
						}
						numbr = o+j;
					}
				}
			if (numbr != -1)	{
				m_ch[0].noteOff(numbr, 700);
			}
		}
		public void keyTyped(KeyEvent e){
		}
	}
}
