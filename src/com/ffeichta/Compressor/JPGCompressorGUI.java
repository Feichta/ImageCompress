package com.ffeichta.Compressor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Creates the GUI of the application and the ActionListener
 * 
 * @author Fabian
 * 
 */
public class JPGCompressorGUI extends JFrame {

	/**
	 * Components for the GUI
	 */
	protected JPanel panelCenter = null;;
	protected JPanel panelSouth = null;
	protected ImageComponent imageComponent = null;

	// Buttons in panelSouth
	protected JButton filechooser = null;
	protected JButton back = null;
	protected JButton forward = null;
	protected JButton play = null;
	protected JButton compress = null;
	protected JButton newPicture = null;
	protected JButton info = null;
	protected JLabel labelPercent = null;
	protected JLabel labelInfo = null;

	// Components for the PopupMenu
	protected JPopupMenu popupMenu = null;
	protected JMenuItem menuItemSave = null;

	/**
	 * Member variables of the image
	 */
	// Absolute path of the selected image, for example: "C:\Fabian\test.txt"
	protected String absolutePath = null;

	// Filename of the selected image, for example: "test.txt"
	protected String filename = null;

	// Path of the selected image, for example: "C:\Fabian\"
	protected String pathToFile = null;

	// The selected image as BufferedImage
	private BufferedImage bufferedImage = null;

	// Quality of the displayed image which goes from 0.0 to 1.0
	protected double actualQuality = 1.0;

	/**
	 * Member variables for threads
	 */
	private PlayThread c1 = null;
	private PlayThread c2 = null;
	private PlayThread c3 = null;
	private PlayThread c4 = null;

	/**
	 * Creates and shows the Frame
	 */
	public JPGCompressorGUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			CompressorException.behandleException(this, e);
		}
		setTitle("Image Compressor - Fabian Feichter TFO \"Max Valier\", Bozen");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1300, 700);
		setLocationRelativeTo(null);
		setResizable(true);
		getContentPane().setLayout(new BorderLayout(0, 0));

		Image image = Toolkit.getDefaultToolkit().getImage(
				getClass().getResource("/com/ffeichta/Images/app.png"));
		setIconImage(image);

		back = new JButton("");
		back.setIcon(new ImageIcon(JPGCompressorGUI.class
				.getResource("/com/ffeichta/Images/back.png")));
		back.setEnabled(false);
		back.setToolTipText("Increase quality");
		back.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						try {
							EventQueue.invokeAndWait(new Runnable() {

								@Override
								public void run() {
									forward.setEnabled(true);
									int percent = Integer.valueOf(labelPercent
											.getText().substring(
													0,
													labelPercent.getText()
															.indexOf("%"))) + 1;
									if (percent <= 100) {
										labelPercent.setText(percent
												+ "% Quality");
										new CompressAndShowThread(
												JPGCompressorGUI.this, false);
										if (percent == 100) {
											back.setEnabled(false);
										}
									}
								}
							});
						} catch (InvocationTargetException
								| InterruptedException e1) {
							CompressorException.behandleException(
									JPGCompressorGUI.this, e1);
						}
					}
				}).start();
			}
		});

		forward = new JButton("");
		forward.setIcon(new ImageIcon(JPGCompressorGUI.class
				.getResource("/com/ffeichta/Images/forward.png")));
		forward.setToolTipText("Reduce quality");
		forward.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						try {
							EventQueue.invokeAndWait(new Runnable() {

								@Override
								public void run() {
									back.setEnabled(true);
									int percent = Integer.valueOf(labelPercent
											.getText().substring(
													0,
													labelPercent.getText()
															.indexOf("%")));
									if (percent <= 1) {
										forward.setEnabled(false);
									}
									new CompressAndShowThread(
											JPGCompressorGUI.this, true);
									labelPercent.setText((percent - 1)
											+ "% Quality");
								}
							});
						} catch (InvocationTargetException
								| InterruptedException e1) {
							CompressorException.behandleException(
									JPGCompressorGUI.this, e1);
						}
					}
				}).start();

			}
		});

		play = new JButton("");
		play.setIcon(new ImageIcon(JPGCompressorGUI.class
				.getResource("/com/ffeichta/Images/play.png")));
		play.setToolTipText("Play");
		play.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						// When you click "Play" the slideshow starts on 99%
						labelPercent.setText("100% Quality");
						actualQuality = 1.0;

						forward.setEnabled(false);
						back.setEnabled(false);
						play.setEnabled(false);
						compress.setEnabled(false);
						newPicture.setEnabled(false);

						/**
						 * There are four threads working parallel (compressing
						 * the pictures). It guarantees you a smooth slideshow
						 * without irregular waits. The time beetween the
						 * pictures is one second.
						 */
						c1 = new PlayThread(JPGCompressorGUI.this);
						c2 = new PlayThread(JPGCompressorGUI.this);
						c3 = new PlayThread(JPGCompressorGUI.this);
						c4 = new PlayThread(JPGCompressorGUI.this);

						for (int i = 0; i < 100; i++) {
							try {
								c1.getCompressThread().join();
							} catch (InterruptedException e1) {
								CompressorException.behandleException(
										JPGCompressorGUI.this, e1);
							}
							try {
								EventQueue.invokeAndWait(new Runnable() {

									@Override
									public void run() {
										imageComponent.setImage(c1
												.getResultImage());
										int percent = Integer
												.valueOf(labelPercent
														.getText()
														.substring(
																0,
																labelPercent
																		.getText()
																		.indexOf(
																				"%")));
										labelPercent.setText((percent - 1)
												+ "% Quality");
									}
								});
							} catch (Exception e1) {
								CompressorException.behandleException(
										JPGCompressorGUI.this, e1);
							}
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e1) {
								CompressorException.behandleException(
										JPGCompressorGUI.this, e1);
							}
							if (i != 99) {
								c1 = c2;
								c2 = c3;
								c3 = c4;
								if (i < 96) {
									c4 = new PlayThread(JPGCompressorGUI.this);
								}
							}
						}

						forward.setEnabled(true);
						play.setEnabled(true);
						compress.setEnabled(true);
						newPicture.setEnabled(true);

						actualQuality = 1.0;
						labelPercent.setText("100% Quality");

						try {
							imageComponent.setImage(absolutePath);
						} catch (IOException e) {
							CompressorException.behandleException(
									JPGCompressorGUI.this, e);
						}
					}
				}).start();
			}
		});

		labelInfo = new JLabel("");

		labelPercent = new JLabel("");

		// Drag and Drop component (DnD)
		imageComponent = new ImageComponent();
		try {
			BufferedImage img = ImageIO.read(JPGCompressorGUI.class
					.getResource("/com/ffeichta/Images/upload.jpg"));
			imageComponent.setImage(img);
			new FileDrop(System.out, imageComponent, new FileDrop.Listener() {
				public void filesDropped(java.io.File[] files) {
					try {
						// If there is more than one file, then choose the first
						absolutePath = files[0].getCanonicalPath();
						int indexBegin = absolutePath.lastIndexOf(".");
						String pathExtension = absolutePath.substring(
								indexBegin, absolutePath.length());
						// The supported image file formats are jpg, jpeg, png,
						// gif and bmp, there could be more than that five, but
						// I tested the application only with this
						if (pathExtension.equalsIgnoreCase(".jpg")
								|| pathExtension.equalsIgnoreCase(".jpeg")
								|| pathExtension.equalsIgnoreCase(".png")
								|| pathExtension.equalsIgnoreCase(".gif")
								|| pathExtension.equalsIgnoreCase(".bmp")) {
							labelInfo = new JLabel("");

							imageComponent.setImage(absolutePath);
							labelPercent.setText("100% Quality");
							back.setEnabled(false);
							forward.setEnabled(true);
							popupMenu.add(menuItemSave);
							imageComponent.setComponentPopupMenu(popupMenu);

							actualQuality = 1.0;
							bufferedImage = imageComponent.getImage();
							filename = absolutePath.substring(
									absolutePath.lastIndexOf("\\") + 1,
									absolutePath.length());
							pathToFile = absolutePath.substring(0,
									absolutePath.lastIndexOf("\\"))
									+ "\\";

							panelSouth.removeAll();
							buildSouthPanel();
						} else {
							labelInfo.setText(" "
									+ pathExtension.replace(".", "")
											.toUpperCase()
									+ " files are not supported");
						}
					} catch (java.io.IOException e) {
						CompressorException.behandleException(
								JPGCompressorGUI.this, e);
					}
				}
			});
		} catch (IOException e) {
			CompressorException.behandleException(JPGCompressorGUI.this, e);
		}

		popupMenu = new JPopupMenu();

		menuItemSave = new JMenuItem("Save picture");
		menuItemSave.setIcon(new ImageIcon(JPGCompressorGUI.class
				.getResource("/com/ffeichta/Images/save.png")));
		menuItemSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser chooser = new JFileChooser(pathToFile);
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(JPGCompressorGUI.this) == JFileChooser.APPROVE_OPTION) {
					new Thread(new Runnable() {
						public void run() {
							try {
								// Add to the quality 0.001, because double
								// values are inaccurate
								JPGImageCompress.compressImage(imageComponent
										.getImage(), chooser.getSelectedFile()
										.toString() + "\\",
										actualQuality + 0.0001);
							} catch (IOException e1) {
								CompressorException.behandleException(
										JPGCompressorGUI.this, e1);
								// Without this you would see the message
								// "Image saved successfully." when you type in
								// an invalid path
								return;
							}
							JOptionPane.showMessageDialog(
									JPGCompressorGUI.this,
									"Image saved successfully.", "Success",
									JOptionPane.INFORMATION_MESSAGE);
						}
					}).start();
				}

			}
		});
		popupMenu.add(menuItemSave);

		compress = new JButton("");
		compress.setIcon(new ImageIcon(JPGCompressorGUI.class
				.getResource("/com/ffeichta/Images/compress.png")));
		compress.setToolTipText("Compress and save");
		this.getRootPane().setDefaultButton(compress);
		compress.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CompressButtonDialog c = new CompressButtonDialog(
						JPGCompressorGUI.this, pathToFile);
				c.setVisible(true);
				c.dispose();
			}
		});

		newPicture = new JButton("");
		newPicture.setIcon(new ImageIcon(JPGCompressorGUI.class
				.getResource("/com/ffeichta/Images/new.png")));
		newPicture.setToolTipText("New picture");
		newPicture.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					BufferedImage img = ImageIO.read(JPGCompressorGUI.class
							.getResource("/com/ffeichta/Images/upload.jpg"));
					imageComponent.setImage(img);
				} catch (IOException e1) {
					CompressorException.behandleException(
							JPGCompressorGUI.this, e1);
				}
				popupMenu.remove(menuItemSave);
				panelSouth.removeAll();
				buildSouthPanelInit();
			}
		});

		info = new JButton("");
		info.setIcon(new ImageIcon(JPGCompressorGUI.class
				.getResource("/com/ffeichta/Images/info.png")));
		info.setToolTipText("About");
		info.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				InfoBox infoBox = new InfoBox();
				infoBox.setVisible(true);
				infoBox.dispose();
			}
		});

		filechooser = new JButton("Choose File");
		filechooser.setFont(new Font("Arial", NORMAL, 30));
		filechooser.setPreferredSize(new Dimension(50, 50));
		filechooser.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					JFileChooser chooser = new JFileChooser();
					FileNameExtensionFilter filter = new FileNameExtensionFilter(
							"*.jpg; *.jpeg; *.png; *.gif; *.bmp", "jpg",
							"jpeg", "png", "gif", "bmp");
					chooser.setFileFilter(filter);
					int returnVal = chooser
							.showOpenDialog(JPGCompressorGUI.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						absolutePath = chooser.getSelectedFile()
								.getAbsolutePath();
						int indexBegin = absolutePath.lastIndexOf(".");
						String pathExtension = absolutePath.substring(
								indexBegin, absolutePath.length());
						if (pathExtension.equalsIgnoreCase(".jpg")
								|| pathExtension.equalsIgnoreCase(".jpeg")
								|| pathExtension.equalsIgnoreCase(".png")
								|| pathExtension.equalsIgnoreCase(".gif")
								|| pathExtension.equalsIgnoreCase(".bmp")) {
							labelInfo = new JLabel("");

							imageComponent.setImage(absolutePath);
							labelPercent.setText("100% Quality");
							back.setEnabled(false);
							forward.setEnabled(true);
							popupMenu.add(menuItemSave);
							imageComponent.setComponentPopupMenu(popupMenu);

							actualQuality = 1.0;
							bufferedImage = imageComponent.getImage();
							filename = absolutePath.substring(
									absolutePath.lastIndexOf("\\") + 1,
									absolutePath.length());
							pathToFile = absolutePath.substring(0,
									absolutePath.lastIndexOf("\\"))
									+ "\\";

							panelSouth.removeAll();
							buildSouthPanel();

							// Without this, the GUI wouldn't update, I don't
							// know the reason of this behavior
							JPGCompressorGUI.this.setSize(1300, 701);
							JPGCompressorGUI.this.setSize(1300, 700);
						} else {
							labelInfo.setText(" "
									+ pathExtension.replace(".", "")
											.toUpperCase()
									+ " files are not supported");
						}
					}
				} catch (IOException e1) {
					CompressorException.behandleException(
							JPGCompressorGUI.this, e1);
				}
			}
		});

		panelCenter = new JPanel();
		panelCenter.setLayout(new BorderLayout(0, 0));
		panelCenter.add(imageComponent, BorderLayout.CENTER);
		getContentPane().add(panelCenter, BorderLayout.CENTER);

		panelSouth = new JPanel();
		panelSouth.setBackground(Color.WHITE);
		panelSouth.setLayout(new BorderLayout(0, 0));
		panelSouth.add(filechooser, BorderLayout.CENTER);
		panelSouth.add(labelInfo, BorderLayout.NORTH);
		getContentPane().add(panelSouth, BorderLayout.SOUTH);

		setVisible(true);
	}

	/**
	 * Builds the Panel with the Buttons on the lower side when an image has
	 * been selected
	 */
	public void buildSouthPanel() {
		JPanel panelSouthWest = new JPanel();
		panelSouthWest.add(back, BorderLayout.WEST);
		panelSouthWest.add(forward, BorderLayout.WEST);
		panelSouthWest.add(play, BorderLayout.WEST);
		panelSouthWest.add(labelPercent, BorderLayout.WEST);
		panelSouth.add(panelSouthWest, BorderLayout.WEST);

		JPanel panelSouthCenter = new JPanel();
		panelSouthCenter.add(compress, BorderLayout.CENTER);
		panelSouth.add(panelSouthCenter, BorderLayout.CENTER);

		JPanel panelSouthEast = new JPanel();
		panelSouthEast.add(newPicture, BorderLayout.EAST);
		panelSouthEast.add(info, BorderLayout.EAST);
		panelSouth.add(panelSouthEast, BorderLayout.EAST);
	}

	/**
	 * Builds the Panel with the FileChooser on the lower side when the
	 * application starts
	 */
	public void buildSouthPanelInit() {
		panelSouth.add(filechooser, BorderLayout.CENTER);

		labelInfo = new JLabel(" ");
		panelSouth.add(labelInfo, BorderLayout.NORTH);

		// Without this, the GUI wouldn't update, I don't
		// know the reason of this behavior
		JPGCompressorGUI.this.setSize(1300, 701);
		JPGCompressorGUI.this.setSize(1300, 700);
	}

	public BufferedImage getBufferedImage() {
		return this.bufferedImage;
	}
}
