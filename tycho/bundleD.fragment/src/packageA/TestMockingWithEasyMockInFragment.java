package packageA;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import bundled2.DummyClass;
import bundled2.IAInterfaceToMock;

public class TestMockingWithEasyMockInFragment
{
    private DummyClass dummyClass;
    
    @Before
    public void setupMocks() {
        dummyClass = EasyMock.createMock( DummyClass.class );
    }
    
    @Test
    public void testMockedClassCreation() {
        dummyClass = EasyMock.createMock( DummyClass.class );
        Assert.assertNotNull( dummyClass );
        System.out.println(dummyClass);
    }
    
    @Test
    public void testBeforeSetupOfMockedClass() {
        Assert.assertNotNull( dummyClass );
        System.out.println(dummyClass);
    }
    
    @Test
    public void testMockedInterfaceCreation() {
        IAInterfaceToMock toMock = EasyMock.createMock( IAInterfaceToMock.class );
        Assert.assertNotNull( toMock );
        System.out.println(toMock);
    }
}
