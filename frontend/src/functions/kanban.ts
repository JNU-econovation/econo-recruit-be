import { DropResult } from '@hello-pangea/dnd';
import { KanbanRowData } from '@/src/stores/kanban/Kanban.atoms';

export const getMovedKanbanData = (
  kanbanData: KanbanRowData[],
  result: DropResult
): KanbanRowData[] => {
  if (!result.destination) return kanbanData;

  if (result.type === 'COLUMN') {
    const fromIndex = result.source.index;
    const toIndex = result.destination.index;

    if (toIndex === fromIndex) return kanbanData;

    const shallow = structuredClone(kanbanData);
    const pickData = shallow.splice(fromIndex, 1);
    shallow.splice(toIndex, 0, ...pickData);

    return shallow;
  }

  if (result.type === 'DEFAULT') {
    const from = result.source;
    const to = result.destination;

    const shallow = structuredClone(kanbanData);
    const pickData = shallow[+from.droppableId].column.splice(from.index, 1);
    shallow[+to.droppableId].column.splice(to.index, 0, ...pickData);

    return shallow;
  }

  return kanbanData;
};