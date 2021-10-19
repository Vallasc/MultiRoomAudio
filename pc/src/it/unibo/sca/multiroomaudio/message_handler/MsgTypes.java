package it.unibo.sca.multiroomaudio.message_handler;

/* 
  Describes the different messages between entities
 */
public class MsgTypes {
	//position registering
	public static int REF_POINT = 00; //[client] sends the different signals strength at a reference point during the offline phase
	public static int SPEAKER_HERE = 01; //[client] same as above but with the poisition of a speaker

	//position handling 
	public static int SEND_DBM = 10; //[client] same as 00 but during the online phase
	public static int SELECT_SPEAKER = 11; //[SLAC] message sent to a speaker to let it know it's the one selected

	//music handling request
	public static int REQ_MUSIC_LIST = 20; //[client] request the music list from the SLAC
	public static int REQ_MUSIC = 21;//[client] request to play a song to the SLAC

	/*NOTE:
		qua sono indeciso in realtà, perchè volendo si potrebbe mettere in comunicazione diretta il client con lo speaker per le operazioni di 
		pausa ecc, solo che poi nel momento in cui cambio stanza lo slac deve prendere lo stato e trasportarlo in un'altra, se lo stato invece
		è mantenuto direttamente sullo SLAC allora il problema non si pone. 
		Comunque faccio i diagrammini tra poco per sta roba.
		*/
	public static int PLAY = 22;
	public static int PAUSE = 23;//[client] music handling, everything sent to the SLAC
	public static int STOP = 24;//[client]
	public static int RESUME = 25;//[client]

	//music handling response
	public static int SEND_MUSIC_LIST = 30;//[SLAC] answer to msg 20
	public static int SEND_MUSIC = 31;//[SLAC] sends a song to the speakers

	//error messages 100-999


	
}