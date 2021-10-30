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



class PianoFrame {
	//위 건반
	private final String[] key1 = {"Q","2","W","3","E","R","5","T","6","Y","7","U","I","9","O","0","P",};
	private final int[] keyE1 = {81,50,87,51,69,82,53,84,54,89,55,85,73,57,79,48,80};
	private JButton[] btn1 = new JButton[17];
	
	//아래건반
	private final String[] key2 = {"Z","S","X","D","C","V","G","B","H","N","J","M",",","L",".",";","/"};
	private final int[] keyE2 = {90,83,88,68,67,86,71,66,72,78,74,77,44,76,46,59,47};
	private JButton[] btn2 = new JButton[17];

	//설명 JLabel
	private JLabel how0 = new JLabel("키보드를 눌러보세요.");
	private JLabel how1 = new JLabel("위아래 방향키: 옥타브 조정");
	private JLabel how2 = new JLabel("좌우 방향키: 음색 조정");
	
	//midi 다루기 위한 변수
	private Synthesizer syn;
	private MidiChannel[] m_ch;
	Piano a, b; //스레드 객체를 위한 Piano변수

	//피아노의 전체적인 틀 잡기
	public PianoFrame() { 
		JFrame f = new JFrame();
		f.setTitle("Virtual Piano 2020");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLayeredPane j = f.getLayeredPane();
		
		//설명라벨 추가
		how0.setBounds(10,140,200,10);
		how1.setBounds(10,100,200,10);
		how2.setBounds(10,120,200,10);
		f.add(how0);
		f.add(how1);
		f.add(how2);
		
		//스레드 추가
		a = new Piano(f, j, key1, btn1, keyE1, 0, 60);
		b = new Piano(f, j, key2, btn2, keyE2, 250, 48);
		a.start();
		b.start();
	}
}