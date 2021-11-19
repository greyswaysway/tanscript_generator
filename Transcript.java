package Assignment2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.*;



/**
* This class generates a transcript for each student, whose information is in the text file.
* 
*
*/

public class Transcript {
	private ArrayList<Object> grade = new ArrayList<Object>();
	private File inputFile;
	private String outputFile;
	
	/**
	 * This the the constructor for Transcript class that 
	 * initializes its instance variables and call readFie private
	 * method to read the file and construct this.grade.
	 * @param inFile is the name of the input file.
	 * @param outFile is the name of the output file.
	 */
	public Transcript(String inFile, String outFile) {
		inputFile = new File(inFile);	
		outputFile = outFile;	
		grade = new ArrayList<Object>();
		this.readFile();
	}// end of Transcript constructor

	/** 
	 * This method reads a text file and add each line as 
	 * an entry of grade ArrayList.
	 * @exception It throws FileNotFoundException if the file is not found.
	 */
	private void readFile() {
		Scanner sc = null; 
		try {
			sc = new Scanner(inputFile);	
			while(sc.hasNextLine()){
				grade.add(sc.nextLine());
	        }      
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			sc.close();
		}		
	} // end of readFile
	
	/**
	 * This method takes the elements in grade
	 * and then uses that info to make an array of
	 * students that are in grade
	 * @return an ArrayList of all the students in grade
	 * @throws InvalidTotalException if any of the student's grade is above 100 or their weight is not 100
	 */
	public ArrayList<Student> buildStudentArray() throws InvalidTotalException{
		ArrayList<Student> studArray = new ArrayList<Student>();
		String name = "";
		String courCode = "";
		double credit = 0.0;
		String studID = "";
		for(int i = 0; i < grade.size(); i++) {//go through every line in grade
			ArrayList<Course>courseTaken = new ArrayList<Course>();
			ArrayList<Assessment>assignment = new ArrayList<Assessment>();
			ArrayList<Integer> weight = new ArrayList<Integer>();
			ArrayList<Double> mark = new ArrayList<Double>();
			ArrayList<String> elementList = new ArrayList<>(Arrays.asList(((String) grade.get(i)).split(",")));//split the line of grade into elements
			courCode = elementList.get(0);  //assign variables with their corresponding values from elementList
			credit = Double.parseDouble(elementList.get(1));
			studID = elementList.get(2);
			name = elementList.get(elementList.size() - 1);
			for (int j = 3; j < elementList.size() - 1; j++) {//cycle through all the assessments in elementList and then add them to the list
				Assessment curAssess = Assessment.getInstance(elementList.get(j).charAt(0), Integer.parseInt(elementList.get(j).substring(1, 3)));
				assignment.add(curAssess);
				int splitZone1 = elementList.get(j).indexOf('(');//find the index where weight and mark of the assessment is stored
				int splitZone2 = elementList.get(j).indexOf(')');
				weight.add(Integer.parseInt(elementList.get(j).substring(1, splitZone1)));//add the assessment weight to the list weight
				mark.add(Double.parseDouble(elementList.get(j).substring(splitZone1 + 1, splitZone2)));//add the mark of the assessment to the list mark
			}
			Course course = new Course(courCode, assignment, credit);//create a new course using the info gathered from the line in grade
			boolean repeat = true;//used to indicate if the student is already in the list or not
			for (int k = 0; k < studArray.size() && repeat; k++) {//check if student is already in studArray
				if(studArray.get(k).getStudID().equals(studID)) {
					repeat = false;//indicate student already exist in the list
					studArray.get(k).addCour(course);//add the course in
					studArray.get(k).addGrade(mark, weight);//get their final grade in the course and add it to their finalGrade
				}
			}
			if(repeat) {//if student wasn't already in the list, add him/her in along with the course
				Student curStud = new Student(studID, name, courseTaken);//create a new student
				curStud.addCour(course);//add the course to the student's course list
				studArray.add(curStud);//add new student to studArray
				studArray.get(studArray.size() - 1).addGrade(mark, weight);//get their final grade in the course and add it to their finalGrade
			}
		}
		return studArray;
	}//end of buildStudentArray
	/**
	 * This method will take the student array created from
	 * buildStudentArray() and then make a transcript in a 
	 * string that is then placed into a text file who's
	 * name is decide by the outputFile variable
	 * @param the ArrayList of students from buildStudentArray
	 */
	public void printTranscript(ArrayList<Student> studArray) {
		String transcript = "";
		for(int i = 0; i < studArray.size() - 1; i++) {//builds a transcript for every student
			transcript += studArray.get(i).getName() + "\t" + studArray.get(i).getStudID();
			transcript += "\r\n--------------------";
			for(int k = 0; k < studArray.get(i).getCours().size(); k++) {
				transcript += "\r\n" + studArray.get(i).getCours().get(k).getCourName() + "\t" + studArray.get(i).getGrades().get(k);
			}
			transcript += "\r\n--------------------";
			transcript += "\r\nGPA: " + studArray.get(i).weightedGPA();
			transcript += "\r\n\r\n";
		}
		try {//tries to print the string into the outputFile
			File transcriptFile = new File(outputFile);
			transcriptFile.createNewFile();
			FileWriter tran = new FileWriter(outputFile);
			tran.write(transcript);
			tran.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}//end of buildTranscript

	
} // end of Transcript

class Student{
	private String studentID;
	private String name;
	private ArrayList<Course> courseTaken;
	private ArrayList<Double> finalGrade;
	
	/**
	 * This is a Student constructor 
	 * that initializes it's variables as blanks
	 */
	public Student() {
		this.studentID = null;
		this.name = null;
		this.courseTaken = new ArrayList<Course>();
		this.finalGrade = new ArrayList<Double>();
	}
	/**
	 * This is a Student constructor that
	 * initializes it's variables with the
	 * given values
	 * @param studID is the ID of the student
	 * @param name is the name of the student
	 * @param courTake is a list of all the courses the student is taking
	 */
	public Student(String studID, String name, ArrayList<Course> courTake) {
		this.studentID = studID;
		this.name = name;
		this.courseTaken = courTake;
		this.finalGrade = new ArrayList<Double>();
	}
	/**
	 * This method takes a list of marks
	 * and a list of their weights and 
	 * calculates the grade you would get
	 * to you finalGrade list
	 * it throws and Exception if your 
	 * weight is not 100 or if your total mark
	 * is greater than 100
	 * @param mark is a list of the student's marks in the course
	 * @param weight is the weight of the assessments in the course
	 * @throws InvalidTotalException if the student's grade is more than 100 and/or the weight of the assessments are not 100
	 * @pre there are equal number of elements in the two list
	 */
	public void addGrade(ArrayList<Double> mark, ArrayList<Integer> weight) throws InvalidTotalException{
		double sum = 0;
		double sumWeight = 0;
		for(int i = 0; i < weight.size(); i++) {
			sum += weight.get(i) / 100.0 * mark.get(i);//calculate your grade according to you mark and weight of the assessment
			sumWeight += weight.get(i);//get your total weight
		}
		if (sum > 100) {//throw exception if your grade is greater than 100
			throw new InvalidTotalException("The sum of your grades is greater than 100");
		}else if (sumWeight != 100) {//throw exception if your total weight is not 100
			throw new InvalidTotalException("The sum of your weight is not 100");
		}else {
			this.finalGrade.add(Math.round(sum * 10.0) / 10.0);//add the grade to your finalGrade if no exception is thrown, rounding it to 1 decimal place
		}
	}//end of addGrade
	/**
	 * This method takes your list of
	 * final grade and calculates
	 * your weightedGPA using your
	 * final marks along with their 
	 * weight in credit
	 * @return finalGPA which is the student's GPA rounded to 1 decimal place
	 */
	public double weightedGPA() {
		double sumFinal = 0.0;
		double finalGPA = 0.0;
		double creditTotal = 0.0;
		for(int i = 0; i < this.finalGrade.size(); i++) {
			creditTotal += this.courseTaken.get(i).getCredit();//get the total amount of credits the student has 
			if(this.finalGrade.get(i) > 90) {		//check what grade point the final grade is, multiply it with credit of
				sumFinal += 9 * this.courseTaken.get(i).getCredit();//the course and then add it to the sum
			}else if(this.finalGrade.get(i) > 80) {
				sumFinal += 8 * this.courseTaken.get(i).getCredit();
			}else if(this.finalGrade.get(i) > 75) {
				sumFinal += 7 * this.courseTaken.get(i).getCredit();
			}else if (this.finalGrade.get(i) > 70) {
				sumFinal += 6 * this.courseTaken.get(i).getCredit();
			}else if (this.finalGrade.get(i) > 65) {
				sumFinal += 5 * this.courseTaken.get(i).getCredit();
			}else if (this.finalGrade.get(i) > 60) {
				sumFinal += 4 * this.courseTaken.get(i).getCredit();
			}else if (this.finalGrade.get(i) > 55) {
				sumFinal += 3 * this.courseTaken.get(i).getCredit();
			}else if (this.finalGrade.get(i) > 50) {
				sumFinal += 2 * this.courseTaken.get(i).getCredit();
			}else if (this.finalGrade.get(i) > 47) {
				sumFinal += 1 * this.courseTaken.get(i).getCredit();
			}else {
				sumFinal += 0;
			}
			
		}
		finalGPA = Math.round(sumFinal/ creditTotal * 10.0) / 10.0;//calculate and round the resulting GPA to 1 decimal
		return finalGPA;
	}//end of weightedGPA
	/**
	 *This method let's you add a course to 
	 *the list of courses the student is taking
	 * @param course is the course you want to add to the list
	 */
	public void addCour(Course course) {
		this.courseTaken.add(course);
	}
	/**
	 *This method let's you change the name
	 *of the student
	 * @param name is the new name of the student
	 */
	public void changeName(String name) {
		this.name = name;
	}
	/**
	 *This method let's you change the ID
	 *of the student
	 * @param newID is the new id of the student
	 */
	public void changeID(String newID) {
		this.studentID = newID;
	}
	/**
	 *This method let's you change the courses the
	 *student is taking
	 * @param courses is the courses you want student to now take
	 */
	public void changeCourses(ArrayList<Course> courses) {
		this.courseTaken = courses;
	}
	/**
	 *This method let's you change the grades
	 *of the student
	 * @param grades are the grades you want the student to now have
	 */
	public void changeGrades(ArrayList<Double> grades) {
		this.finalGrade = grades;
	}
	/**
	 * This method returns the 
	 * student's ID
	 * @return the student's ID
	 */
	public String getStudID() {
		return this.studentID;
	}
	/**
	 *This method returns the 
	 *student's name
	 * @return name of the student
	 */
	public String getName() {
		return this.name;
	}
	/**
	 *This method returns the list of
	 *all the courses the student is taking
	 * @return the course list of the student
	 */
	public ArrayList<Course> getCours(){
		return this.courseTaken;
	}
	/**
	 *This method returns the student's
	 *final grades
	 * @return the student's final grades
	 */
	public ArrayList<Double> getGrades(){
		return this.finalGrade;
	}
}//end of Student
/**
 * This exception gets triggered when 
 * the weight is not 100 or the total
 * mark of a student is greater than 100
 * telling you which one is wrong
 */
class InvalidTotalException extends Exception {
	public InvalidTotalException() {
		super();
	}
	public InvalidTotalException(String message) {
		super(message);
	}
}

class Course {
	private String code;
	private ArrayList<Assessment> assignment;
	private double credit;
	
	/**
	 * This constructor initialize the 
	 * variables in a new Course 
	 * object as blanks
	 */
	public Course() {
		this.code = null;
		this.assignment = new ArrayList<Assessment>();
		this.credit = 0.0;
	}
	/**
	 * This constructor initialize the 
	 * variables in a new Course
	 * object with the values given
	 * @param code is the course code
	 * @param assignment is the assessments in the course
	 * @param credit is the number of credits the course is worth
	 */
	public Course(String code, ArrayList<Assessment> assignment, double credit) {
		this.code = code;
		this.assignment = assignment;
		this.credit = credit;
	}
	/**
	 * This constructor initializes
	 * the variables in a new Course
	 * object to match the given 
	 * courses
	 * @param other is the course you want to copy
	 */
	public Course(Course other) {
		this.code = other.code;
		this.assignment = other.assignment;
		this.credit = other.credit;
	}
	/**
	 * This method check is 2 courses
	 * are exactly the same and returns 
	 * true if they are
	 * @param obj is the course you want to compare with
	 * @return true if they equal, false otherwise
	 */
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Course other = (Course) obj;
		if (! this.code.equals(other.code)) {
			return false;
		}
		if (! this.assignment.equals(other.assignment)) {
			return false;
		}
		if(this.credit != other.credit) {
			return false;
		}
		return true;
	}//end of equals
	/**
	 * This method returns the
	 * number of credits the course is worth
	 * @return number of credits the course is worth
	 */
	public double getCredit() {
		return this.credit;
	}
	/**
	 * This method returns the
	 * name of the course
	 * @return name of the course
	 */
	public String getCourName() {
		return this.code;
	}
	/**
	 * This method returns a list of
	 * the assessments in the course
	 * @return assessments in the course
	 */
	public ArrayList<Assessment> getAssessments(){
		return this.assignment;
	}
	/**
	 *This method let's you change the number
	 *of credits a course is worth
	 * @param credits is the number of credits you want to course to be worth
	 */
	public void changeCredit(double credits) {
		this.credit = credits;
	}
	/**
	 *This method let's you change the name 
	 *of the course
	 * @param courName is the new name of the course
	 */
	public void changeCourName(String courName) {
		this.code = courName;
	}
	/**
	 *This method let's you change the assessments
	 *the course has
	 * @param assess is the list of new assessments the course will have
	 */
	public void changeAssessments (ArrayList<Assessment> assess) {
		this.assignment = assess;
	}
}//end of Course

class Assessment {
	private char type;
	private int weight;
	
	/**
	 * This is a constructor for 
	 * Assessment that initialize
	 * it's variables as their default
	 * value
	 */
	private Assessment(){
		this.type = ' ';
		this.weight = 0;
	}
	/**
	 * This is a constructor that initialize the
	 * variable in a new Assessment object as 
	 * the values given
	 * @param type is the type of assessment it is
	 * @param weight is the weight of the assessment
	 */
	private Assessment(char type, int weight) {
		this.type = type;
		this.weight = weight;
	}
	/**
	 * This method calls in the Assessment
	 * constructor to make a new Assessment
	 * object
	 * @param type is the type of the assessment
	 * @param weight is the weight of the assessment
	 * @return a new Assessment object created by the constructor
	 */
	public static Assessment getInstance(char type, int weight) {
		return new Assessment(type, weight);
	}
	/**
	 * This method returns true if
	 * all the variables in 2
	 * Assessments are equal
	 * @param obj is the Assessment you want to compare with
	 * @return true if they are equal, false otherwise
	 */
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Assessment other = (Assessment) obj;
		if(type != other.type) {
			return false;
		}
		if(weight != other.weight) {
			return false;
		}
		return true;
	}//end of equals
	/**
	 * This method returns the
	 * weight of the assessment
	 * @return weight of the assessment
	 */
	public int getWeight(){
		return this.weight;
	}
	/**
	 * This method returns the
	 * type of the assessment
	 * @return the type of the assessment
	 */
	public char getType() {
		return this.type;
	}
	/**
	 * This method lets you change
	 * the weight of the assessment
	 * variable in the class
	 * @param the new weight of the assessment
	 */
	public void changeWeight(int weight) {
		this.weight = weight;
	}
	/**
	 * This method lets you change 
	 * the type of the assessment
	 * @param type is the new type of the assessment
	 */
	public void changeType(char type) {
		this.type = type;
	}
}//end of Assessment

