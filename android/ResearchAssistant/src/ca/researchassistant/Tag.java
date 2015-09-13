package ca.researchassistant;

public class Tag {
	int partindex;
	int sectionid;
	int surveyid;
	Tag(int pid, int survey, int section) {
		partindex = pid;
		surveyid = survey;
		sectionid = section;
	}
}
