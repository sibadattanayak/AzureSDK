import java.io.File;
import java.util.Date;

import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.Disk;
import com.microsoft.azure.management.compute.VirtualMachine;
import com.microsoft.azure.management.compute.VirtualMachineSizeTypes;
import com.microsoft.azure.management.network.Network;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.fluentcore.model.Creatable;
import com.microsoft.azure.management.samples.Utils;
import com.microsoft.rest.LogLevel;

/*
Azure Compute sample for managing virtual machines - 
- Create a virtual machine 
- Start a virtual machine 
- Restart a virtual machine 
- Stop a virtual machine 
- List virtual machines 
- Delete a virtual machine
 */

public class ManageVirtualMachine {
	private static VirtualMachine windowsVM =null;
	
	private ManageVirtualMachine() {
		
	}

	private static void generateVM(Azure azure) {
		final Region region = Region.ASIA_SOUTHEAST;
		final String windowsVMName = "SIBA-VM");
		final String rgName = "TC_POC";
		final String userName = "tc-poc-user";
		final String password = "Techchefs@123";
		Date t1 = new Date();
		System.out.println("Creating a Windows VM");
		 windowsVM = azure.virtualMachines().define(windowsVMName).withRegion(region)
				.withExistingResourceGroup(rgName).withNewPrimaryNetwork("10.0.0.0/28")
				.withPrimaryPrivateIPAddressDynamic().withoutPrimaryPublicIPAddress()
				.withLatestWindowsImage("MicrosoftWindowsDesktop", "Windows-10", "rs5-pro")
				.withAdminUsername(userName).withAdminPassword(password)
				.withSize(VirtualMachineSizeTypes.STANDARD_B1S).create();
		Date t2 = new Date();
		System.out.println("Created VM: (took " + ((t2.getTime() - t1.getTime()) / 1000) + " seconds) " + windowsVM.id());

	}
	
	
	/**
	 * Main function which runs the actual sample.
	 * 
	 * @param azure instance of the azure client
	 * @return true if sample runs successfully
	 */
	
	
	public static boolean runSample(Azure azure) {
		try {
			// Restart the virtual machine

			System.out.println("Restarting VM: " + windowsVM.id());

			windowsVM.restart();

			System.out.println("Restarted VM: " + windowsVM.id() + "; state = " + windowsVM.powerState());

			// Stop (powerOff) the virtual machine

			System.out.println("Powering OFF VM: " + windowsVM.id());

			windowsVM.powerOff();

			System.out.println("Powered OFF VM: " + windowsVM.id() + "; state = " + windowsVM.powerState());

			// Delete the virtual machine
			System.out.println("Deleting VM: " + windowsVM.id());

			azure.virtualMachines().deleteById(windowsVM.id());

			System.out.println("Deleted VM: " + windowsVM.id());
			
			return true;
		} catch (Exception f) {

			System.out.println(f.getMessage());
			f.printStackTrace();

		} finally {

			try {
				System.out.println("Deleting Resource Group: " + rgName);
				azure.resourceGroups().deleteByName(rgName);
				System.out.println("Deleted Resource Group: " + rgName);
			} catch (NullPointerException npe) {
				System.out.println("Did not create any resources in Azure. No clean up is necessary");
			} catch (Exception g) {
				g.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Main entry point.
	 * 
	 * @param args the parameters
	 */

	public static void main(String[] args) {
	
		try {
		
			System.err.println("Connecting to your AZURE account ...");

			final File credFile = new File("/home/sibadattanayak/Desktop/application.properties");

			Azure azure = Azure.configure().withLogLevel(LogLevel.NONE).authenticate(credFile)
					.withDefaultSubscription();
			System.out.println("Selected subscription: " + azure.subscriptionId());
			generateVM(azure);
			//runSample(azure);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	
}
