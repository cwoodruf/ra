# ra
## Research Assistant Questionnaire Compiler

Research Assistant is a prototype toolset for developing and deploying questionnaires.

The core of the system is a questionnaire scripting language detailed in website/survey/surveylanguage.txt. 
This language is compiled into a JSON object that is used by the website for editing and preview
and by the mobile app to display the questionnaire.

The mobile app interacts with an external website to send updates. All updates are authenticated
via a nonce system. 

Saved questionnaire datasets can be downloaded and displayed in SPSS. 

