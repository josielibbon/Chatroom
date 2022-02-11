# Project 3 : GWack Slack Simulator

## Help Received

Please document any help you received in completing this lab. Note that the what you submit should be your own work. Refer to the syllabus for more details. 


## Describe the OOP design of your GWack Slack Simulator

Please provide a short description of your programming progress

I split up the project into 4 parts, the client code, server code, client GUI, and server GUI that I planned on making for extra credit. The client GUI looks and operates just as how is stated in the project specs. A lot of threading had to be utilized for this project, and using printwriter and bufferedreaders were vital to making my code work. It took a lot of testing to determine how to broadcast messages to all clients, and it took me a while to finally understand how to send the secret and username to Professor Aviv's server. If I had more time, I would've liked my error messages to be nice-looking pop-up windows, and I think it would be interesting to experiment with how to direct message over these servers. Overall, I think this project really helped me better understand threading and how networking works in concurrency with said threads.

## What additional features did you attempt and how can we test them

For an additional feature, I created a GUI for the server we had to create. The GUI makes it easy to input a port number and start up the server, and keeps a convenient event log that informs the server-keeper of when clients join and leave the server, as well as when the server starts and stops. It also has a text area that holds all messages sent in the server from the time it is started to the time it ends, complete with timestamps of messages sent. To test this GUI, you run "java GWackChannelGUI" and it should pop up. Another thing I added was timestamps to the clientside when connected to a localhost server, which I think is a nice detail. Furthermore, I implemented default settings, in which if a server is opened without specifying the port in the commandline, it will automatically open port 1500. 


