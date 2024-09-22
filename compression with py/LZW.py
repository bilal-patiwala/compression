import struct

DictSize=256
def main():
    global DictSize
    file = open("../test1.txt", 'rb')
    lz = {}
    unique_list = []
    index = 1
    for i in range(DictSize):
        lz.__setitem__(str(chr(i)), i)
    
    encoded_list = encode(lz, index)
    print(encoded_list)
    delta_encoded_list = delta_encoding(encoded_list)
    print(delta_encoded_list)
    compress(delta_encoded_list)

def encode(lz, index):
    global DictSize
    file = open("../test1.txt", 'rb')
    encounter = ""
    encoded_list = []
    for c in file.read():
        c = str(chr(c))
        if lz.__contains__(encounter+c):
            encounter+=c
        else:
            encoded_list.append(lz[encounter])
            lz.__setitem__(encounter+c, DictSize)
            DictSize+=1
            encounter = c

    return encoded_list
            

def compress(encoded_list):
    file = open("../out.txt", 'wb')
    for value in encoded_list:
        file.write(struct.pack('i',value))
    file.close()


def delta_encoding(encoded_list):
    delta_encoded_list = [encoded_list[0]]
    index = 0
    for data in encoded_list[1:]:
        delta_encoded_list.append(data-encoded_list[index])
        index+=1
    
    return delta_encoded_list

main()