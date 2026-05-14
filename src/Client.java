import java.io.*;
import java.net.*;


class Client {
	public static void main(String[] args) {
         
	Socket MyClient;
	BufferedInputStream input;
	BufferedOutputStream output;
    	int[][] board = new int[8][8];
	AiPlayer aiPlayer = null;
	PlayerColor myColor = null;
	Move lastAttemptedMove = null;
	int[][] boardBeforeLastAttempt = null;
	final int serverPort = 8888;
	String serverAddress = args.length > 0 ? args[0] : "localhost";
	
	try {
		System.out.println("Connexion au serveur " + serverAddress + ":" + serverPort);
		MyClient = new Socket(serverAddress, serverPort);

	   	input    = new BufferedInputStream(MyClient.getInputStream());
		output   = new BufferedOutputStream(MyClient.getOutputStream());
	   	while(1 == 1){
			char cmd = 0;
		   	
            cmd = (char)input.read();
            System.out.println(cmd);
            // Debut de la partie en joueur blanc
            if(cmd == '1'){
                myColor = PlayerColor.fromServerStartCommand(cmd);
                aiPlayer = new AiPlayer(myColor, 4);
                byte[] aBuffer = new byte[1024];
				
		    int size = input.available();
		    //System.out.println("size " + size);
		    input.read(aBuffer,0,size);
            String s = new String(aBuffer).trim();
            System.out.println(s);
            String[] boardValues;
            boardValues = s.split(" ");
                int x=0,y=0;
                for(int i=0; i<boardValues.length;i++){
                    board[x][y] = Integer.parseInt(boardValues[i]);
                    x++;
                    if(x == 8){
                        x = 0;
                        y++;
                    }
                }

                System.out.println("Nouvelle partie! Vous jouer blanc, entrez votre premier coup : ");
				Move move = aiPlayer.chooseMove(board);
				if (move == null) {
					break;
				}
				lastAttemptedMove = move;
				boardBeforeLastAttempt = new Board(board).toArrayCopy();
				Board localBoard = new Board(board);
				localBoard.applyMoveInPlace(move);
				board = localBoard.toArrayCopy();
				String moveText = move.toServerString();
				output.write(moveText.getBytes(),0,moveText.length());
				output.flush();
            }
            // Debut de la partie en joueur Noir
            if(cmd == '2'){
				myColor = PlayerColor.fromServerStartCommand(cmd);
				aiPlayer = new AiPlayer(myColor, 4);
                System.out.println("Nouvelle partie! Vous jouer noir, attendez le coup des blancs");
                byte[] aBuffer = new byte[1024];
				
				int size = input.available();
				//System.out.println("size " + size);
				input.read(aBuffer,0,size);
                String s = new String(aBuffer).trim();
                System.out.println(s);
                String[] boardValues;
                boardValues = s.split(" ");
                int x=0,y=0;
                for(int i=0; i<boardValues.length;i++){
                    board[x][y] = Integer.parseInt(boardValues[i]);
                    x++;
                    if(x == 8){
                        x = 0;
                        y++;
                    }
                }
            }


			// Le serveur demande le prochain coup
			// Le message contient aussi le dernier coup joue.
	    if(cmd == '3'){
		byte[] aBuffer = new byte[16];
				
		int size = input.available();
		System.out.println("size :" + size);
		input.read(aBuffer,0,size);
				
		String s = new String(aBuffer).trim();
		System.out.println("Dernier coup :"+ s);
		Move opponentMove = Move.fromServerString(s);
		if (opponentMove != null) {
			Board localBoard = new Board(board);
			if (aiPlayer != null && aiPlayer.isLegalMove(board, opponentMove, myColor.opposite())) {
				localBoard.applyMoveInPlace(opponentMove);
				board = localBoard.toArrayCopy();
			}
		}
		System.out.println("Entrez votre coup : ");
		Move move = aiPlayer.chooseMove(board);
		if (move == null) {
			break;
		}
		lastAttemptedMove = move;
		boardBeforeLastAttempt = new Board(board).toArrayCopy();
		Board localBoard = new Board(board);
		localBoard.applyMoveInPlace(move);
		board = localBoard.toArrayCopy();
		String moveText = move.toServerString();
		output.write(moveText.getBytes(),0,moveText.length());
		output.flush();
				
	     }
			// Le dernier coup est invalide
			if(cmd == '4'){
				System.out.println("Coup invalide, entrez un nouveau coup : ");
				if (boardBeforeLastAttempt != null) {
					board = new Board(boardBeforeLastAttempt).toArrayCopy();
				}
				Move move = aiPlayer.chooseMove(board, lastAttemptedMove);
				if (move == null) {
					break;
				}
				lastAttemptedMove = move;
				boardBeforeLastAttempt = new Board(board).toArrayCopy();
				Board localBoard = new Board(board);
				localBoard.applyMoveInPlace(move);
				board = localBoard.toArrayCopy();
				String moveText = move.toServerString();
				output.write(moveText.getBytes(),0,moveText.length());
				output.flush();
				
			}
            // La partie est terminée
	    if(cmd == '5'){
                byte[] aBuffer = new byte[16];
                int size = input.available();
                input.read(aBuffer,0,size);
		String s = new String(aBuffer).trim();
		System.out.println("Partie Terminé. Le dernier coup joué est: "+s);
		break;
				
	    }
        }
	}
	catch (IOException e) {
   		System.out.println(e);
	}
	
    }
}

