
export class FileCustom {
  name: string;
  status: string;
  size: string;
  type: string;


  constructor(name: string, status: string, size: string, type: string) {
    this.name = name;
    this.status = status;
    this.size = size;
    this.type = type;
  }
}
