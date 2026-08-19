type MealProgressCardProps = {
    title: string;
    eaten: number;
    remaining: number;
    total: number;
};

export default function MealProgressCard({
                                             title,
                                             eaten,
                                             remaining,
                                             total,
                                         }: MealProgressCardProps) {
    return (
        <section className="meal-progress-card">
            <h3>{title}</h3>
            <strong>
                {eaten} / {total} eaten
            </strong>
            <span>{remaining} remaining</span>
        </section>
    );
}