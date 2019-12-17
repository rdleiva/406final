package skybox;

public class VInfo
{
	//vertices
	public float vx = 0.0f;
	public float vy = 0.0f;
	public float vz = 0.0f;
	
	//normals
	public float nx = 0.0f;
	public float ny = 0.0f;
	public float nz = 0.0f;
	
	//texture coordinates
	public float uvx = 0.0f;
	public float uvy = 0.0f;
	
	//constructors
	public VInfo(float x, float y, float z)
	{
		vx = x;
		vy = y;
		vz = z;
	}
	public VInfo(float x, float y, float z, float ux, float uy)
	{
		vx = x;
		vy = y;
		vz = z;
		uvx = ux;
		uvy = uy;
	}
	public VInfo(double x, double y, double z, double ux, double uy)
	{
		vx = (float)x;
		vy = (float)y;
		vz = (float)z;
		uvx = (float)ux;
		uvy = (float)uy;
	}
	
	//helper function for printing the information in VInfo
	public void printOut()
	{
		System.out.print("(");
		System.out.print(vx);
		System.out.print(", ");
		System.out.print(vy);
		System.out.print(", ");
		System.out.print(vz);
		System.out.print(") (");
		System.out.print(uvx);
		System.out.print(", ");
		System.out.print(uvy);
		System.out.print(")\n");
	}
}
