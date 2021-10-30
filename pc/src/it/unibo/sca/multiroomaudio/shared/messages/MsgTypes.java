package it.unibo.sca.multiroomaudio.shared.messages;

/* 
  Describes the different messages between entities
 */
public enum MsgTypes {
	HELLO, //[client] announces itself to the server in broadcast
	HELLO_BACK,//[server] answers to the client to let him discover the IP
	SPECS, //[client] sends it's MAC_ADDR and if it's a speaker or a client to the SLAC
	//position registering
	REF_POINT, //[client] sends the different signals strength at a reference point during the offline phase
	SPEAKER_HERE, //[client] same as above but with the poisition of a speaker

	//position handling 
	SET_ROOM, //[client] same as 00 but during the online phase
	SELECT_SPEAKER, //[SLAC] message sent to a speaker to let it know it's the one selected

	//music handling request
	REQ_MUSIC_LIST, //[client] request the music list from the SLAC
	REQ_MUSIC,//[client] request to play a song to the SLAC

	/*NOTE:
		qua sono indeciso in realtà, perchè volendo si potrebbe mettere in comunicazione diretta il client con lo speaker per le operazioni di 
		pausa ecc, solo che poi nel momento in cui cambio stanza lo slac deve prendere lo stato e trasportarlo in un'altra, se lo stato invece
		è mantenuto direttamente sullo SLAC allora il problema non si pone. 
		Comunque faccio i diagrammini tra poco per sta roba.
		*/
	PLAY,
	PAUSE,//[client] music handling, everything sent to the SLAC
	STOP,//[client]
	RESUME,//[client]

	//music handling response
	SEND_MUSIC_LIST,//[SLAC] answer to msg 20
	SEND_MUSIC//[SLAC] sends a song to the speakers

}