import struct
import json
class CharacterModel:
    def __init__(self, low, high):
        self.low = low
        self.high = high

     
class MetaData:
    def __init__(self):
        self.__characterProbability = None
        self.__characterScale = None

    def setCharacterProbability(self, characterProbability):
        self.__characterProbability = characterProbability

    def getCharacterProbability(self):
        return self.__characterProbability

    def setCharacterProbability(self, characterScale):
        self.__characterScale = characterScale

    def getCharacterProbability(self):
        return self.__characterScale

def calculateProbability(totalSymbols, c_list):
    cummulativeProbability = dict()
    probablity = 0.0

    for entry in c_list:
        prob = entry['value'] / totalSymbols
        probablity+=prob
        cummulativeProbability.__setitem__(entry['key'], probablity)
        
    return cummulativeProbability

def scaleCharacter(characterProbability):
    count = 0.0
    characterScale = dict()
    for entry in characterProbability:
        characterScale.__setitem__(entry['key'], CharacterModel(count, entry['value']))
        count = entry['value']

    return characterScale


def main():
    file = open("../idea.txt" , 'r')
    c_map = dict()
    totalSymbols = 0
    for i in file.read():
        if c_map.__contains__(i):
            c_map[i] = c_map[i]+1
        else:
            c_map.__setitem__(i, 1)
        
        totalSymbols+=1

    file.close()
    c_list = [{"key":key, "value":value} for key, value in c_map.items()]
    c_list = sorted(c_list, key=lambda x:x['key'])
    print(c_list)

    characterProbability = calculateProbability(totalSymbols, c_list)
    characterProbability = [{"key":key, "value":value} for key, value in characterProbability.items()]
    print(characterProbability)
    characterScale = scaleCharacter(characterProbability)
    # print(characterScale)


    encoded_number = encode(characterScale)
    print(encoded_number)

    file = open("../out.txt","wb")
    file.write(struct.pack('f',encoded_number))

    file.close()

def encode(characterScale):
    file = open('../idea.txt', 'r')
    low = 0.0
    high = 1.0
    for i in file.read():
        c_range = high-low
        high = low + c_range * highRange(i, characterScale)
        low = low + c_range * lowRange(i, characterScale)
    
    return low

def highRange(ch, characterScale):
    c = characterScale[ch]
    return c.high

def lowRange(char, characterScale):
    c = characterScale[char]
    return c.low

main()