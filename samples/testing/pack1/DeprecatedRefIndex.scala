
/* Testing why some ref. index entries get striked when it shouldn't
 */

class A_IamNotDeprecated {
  // But all my methods are...
  @deprecated def A_x = 1;
  @deprecated def A_y = 1;
}

class AA_IamNotDeprecated {
  // And neither are my methods
  def AA_x = 1;
  def AA_y = 1;
}

@deprecated
class A_IamDeprecated {
  // And all my methods are...
  @deprecated def A_x = 1;
  @deprecated def A_y = 1;
}

@deprecated
class AA_IamDeprecated {
  // But not my methods
  def AA_x = 1;
  def AA_y = 1;
}