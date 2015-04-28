package lt.indrasius.nashorn

import org.specs2.mutable.SpecificationWithJUnit

/**
 * Created by mantas on 15.4.28.
 */
class FileSystemFunctionsTest extends SpecificationWithJUnit {
  "FileSystemFunctions" should {
    "read file" in {
      val file = getClass.getClassLoader.getResource("test/hello.txt").getPath
      val fs = new FileSystemFunctions()

      fs.readFile(file) must_== "Hello World!"
    }
  }
}
