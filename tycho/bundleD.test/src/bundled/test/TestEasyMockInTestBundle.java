package bundled.test;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Test;

import bundled2.IAInterfaceToMock;

public class TestEasyMockInTestBundle
{
    @Test
    public void testMockingOfLocalTestBundleInterface() {
        IAnotherInterfaceToMock toMock = EasyMock.createMock( IAnotherInterfaceToMock.class );
        Assert.assertNotNull( toMock );
        System.out.println(toMock);
        
    }
    
    @Test
    public void testMockingOfInterfaceInBundleUnderTest() {
        IAInterfaceToMock toMock = EasyMock.createMock( IAInterfaceToMock.class );
        Assert.assertNotNull( toMock );
        System.out.println(toMock);
    }
}
