# Chat_System
Description: A chat system allows multiple groups of users to chat. A chat coordinator resides at a well-known network address, uses UDP for communication with chat clients, sets              up chat servers for each chat session, and maintains a chat session directory. There is one chat server per chat session. A chat server uses TCP for communication                  with clients. A chat client allows users to start, join, and leave a chat session. 

The following is the steps to compile a chat system:
1. To start a new chat session; follow the following two steps:
  1. Compile the Coordinator class first
    Format: java Coordinator
    It will prompt for a session ID (a server address and a server port).
  2. Then, compile the Client class
    Format: java Client
    Choose option #1: to START. It will also prompt for a session ID (a server address and a server
    port). 
2. To join a chat, simply compile Client.java. (Format: java Client) Then, choose option #2: to JOIN a
    chat session.
3. To leave a chat session simply type “exit” at anytime during a chat session (terminate TCP connection).
    Also, to exit the chat system program type option #3: to EXIT (terminate UDP connection).
