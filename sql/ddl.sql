create unlogged table payments (
    correlation_id char(36) unique,
    amount decimal not null,
    payment_processor int not null
);

create unlogged table payment_processor_priority (
    payment_processor int not null,
    updated_at timestamp not null
);