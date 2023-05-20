package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {
	private Scanner scanner = new Scanner(System.in);
	private Project curProject = null;
	// @formatter:off
	private List<String> operations = List.of(
			"1) Add a project",
			"2) List projects",
			"3) Select a project",
			"4) Update project details",
			"5) Delete a project"
			
			);
	// @formatter: on
	
	
	
	private ProjectService projectService = new ProjectService();
	

	public static void main(String[] args) {
		new ProjectsApp().processUserSelections();	
	}

	private void processUserSelections() {
		boolean done = false;
		
		while (!done) {
			try {
				
				int selection = getUserSelection();
				
				switch(selection) {
				
				case -1: done = exitMenu();
						 break;
						
				case 1: createProject();
						break;
				
				case 2: listProjects();
						break;
				case 3: selectProject();
						break;
				case 4: updateProjectDetails();
						break;
				case 5: deleteProject();
						break;
						
				default: 
					System.out.println("\n" + selection + " is not a valid selection. Try again.");
				
				}
				
			}
			
			catch(Exception e) {
				System.out.println("\nError: " + e + " Try again.");
			}
		}
	}

	private void deleteProject() {
		listProjects();
		
		Integer projectID = getIntInput("Enter the project ID you wish to delete");
		
		
	    if (curProject == null) {
	        System.out.println("Invalid project ID selected.");
	    }
	    

	    	projectService.deleteProject(projectID);
	    
	    	System.out.println("Project succesfully deleted.");
	  
	    
	    if (curProject.getProjectId() == projectID) {
	    	curProject = null;
	    }
	    	
	    
		
	}

	private void updateProjectDetails() {
		if(Objects.isNull(curProject)) {
			System.out.println("\nPlease select a project.\n\n");
			return;
		}
		
		String projectName = getStringInput("Enter the project name [" + curProject.getProjectName() + "]");
		System.out.println();
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours [" + curProject.getEstimatedHours() + "]");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours [" + curProject.getActualHours() + "]");
		Integer difficulty = curProject.getDifficulty();
		
		try {
		
		do  {
			difficulty = getIntInput("Enter the project difficulty (1-5) [" + curProject.getDifficulty() + "]");
		} while (difficulty < 1 || difficulty > 5 && difficulty != null);
		
		} catch(Exception e) {
			difficulty = 0;
		}
		
		String notes = getStringInput("Enter the project notes");
		
		Project project = new Project();
		
		project.setProjectName(Objects.isNull(projectName)
				? curProject.getProjectName() : projectName);
		
		project.setEstimatedHours(Objects.isNull(estimatedHours)
				? curProject.getEstimatedHours() : estimatedHours);
		
		project.setActualHours(Objects.isNull(actualHours)
				? curProject.getActualHours() : actualHours);
		
		project.setNotes(Objects.isNull(notes)
				? curProject.getNotes() : notes);
		
		project.setDifficulty(difficulty == 0
				? curProject.getDifficulty() : difficulty);
		
		project.setProjectId(curProject.getProjectId());
		
		projectService.modifyProjectDetails(project);
		
		curProject = projectService.fetchProjectById(curProject.getProjectId());
		
		
	}

	private void selectProject() {
		listProjects();
		Integer projectID = getIntInput("Enter a project ID to select a project");
		curProject = null;
		curProject = projectService.fetchProjectById(projectID);
		
	    if (curProject == null) {
	        System.out.println("Invalid project ID selected.");
	    }
	}

	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();
		
		System.out.println("\nProjects:");
		
		projects.forEach(project -> System.out.println(" "+ project.getProjectId() + ": " + project.getProjectName()));
		
	}

	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = 0;
		
		while (difficulty < 1 || difficulty > 5) {
			difficulty = getIntInput("Enter the project difficulty (1-5)");
		}
		
		String notes = getStringInput("Enter the project notes");
		
		Project project = new Project();
		
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: " + dbProject);
		
	}

	private boolean exitMenu() {
		System.out.println("\nExiting the menu. Goodbye!");
		return true;
	}

	private int getUserSelection() {
		printOperations();
		Integer input = getIntInput("Enter a menu selection");
		
		if (input == null) {
			return -1;
	} else {
		return input;
	}
	}

	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);
		
		if (input == null || input.isEmpty()) {
			return null;
		}
		
		
		try {
			return new BigDecimal(input).setScale(2);
		}
		catch(NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number.");
		}
	}

	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);
		
		if (input == null || input.isEmpty()) {
			return null;
		}
		
		try {
			return Integer.valueOf(input);
		}
		catch(NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		}
	}




	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String input = scanner.nextLine();
		
		if (input.isBlank()) {
	        return null;
	    } else {
	        return input.trim();
	    }
	}

	private void printOperations() {
	 System.out.println("These are the available selections. Press the Enter key to quit");
	    for (String operation : operations) {
	        System.out.println("    " + operation);
	    }
	    if(Objects.isNull(curProject)) {
	    	System.out.println("\nYou are not working with a project.");
	    	
	    }else {
	    	System.out.println("\nYou are working with project: " + curProject);
	    }
	}
} 
