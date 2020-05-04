pragma solidity >= 0.4.0 < 0.7.0;

contract Test {

    string name;
    uint age;

    function setName(string memory newName) public {
        name = newName;
    }

    function getName() public view returns (string memory) {
        return name;
    }

    function setAge(uint newAge) public {
        age = newAge;
    }

    function getAge() public view returns (uint){
        return age;
    }
}