package app.se329.project2.model;

public class ProjectContext {
	
	private static ProjectContext projectContext = null;

	public static ProjectContext getProjectContext(){
		return projectContext==null ? projectContext = new ProjectContext() : projectContext;
	}

}
